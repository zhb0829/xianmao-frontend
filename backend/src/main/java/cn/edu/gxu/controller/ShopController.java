package cn.edu.gxu.controller;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.OrderDto;
import cn.edu.gxu.controller.dto.PersonInfoDto;
import cn.edu.gxu.dao.OrderMapper;
import cn.edu.gxu.dao.UserInfoMapper;
import cn.edu.gxu.dao.UserLogMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.model.Order;
import cn.edu.gxu.service.*;

import cn.edu.gxu.utils.TokenUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.Web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    CommodityService commodityService;
    @Resource
    OrderService orderService;
    @Resource
    PersonInfoService   personInfoService;
    @Resource
    ImageToBase64  imageToBase64;
    @Resource
    CollectService collectService;
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    OrderMapper orderMapper;
    @Resource
    UserLogMapper userLogMapper;

    @GetMapping("/selectByID")
    public List<Map<String,Object>> selectByID()
    {
        int  userid = TokenUtils.getCurrentUser().getId();
        return commodityService.SelectByID(userid);
    }

    @PostMapping("/generateOrder")
    public OrderDto generateOrder(int commodityId) {
            int userId = TokenUtils.getCurrentUser().getId();
            CommodityInfo commodityInfo = commodityService.getById(commodityId);
            if (commodityInfo==null){
                throw  new ServiceException(Constants.CODE_400,"商品已下架");
            }
            PersonInfoDto sellerInfoDto = personInfoService.getPersonInfoDto(commodityInfo.getSellerId());
            if (sellerInfoDto==null){
                throw  new ServiceException(Constants.CODE_400,"商品已下架");
            }
            PersonInfoDto personInfoDto = personInfoService.getPersonInfoDto(userId);
            if (personInfoDto==null){
                throw  new ServiceException(Constants.CODE_500,"登录信息错误");
            }
            Order order = new Order();
            order.setOrderStatus("未支付");
            order.setCommodityId(commodityId);
            order.setUserid(userId);
            order.setSubmitTime(new DateTime().toTimestamp());
            order.setLogisticsStatus("未发货");
            order.setSubmitPrices(commodityInfo.getPrice());
            order.setSellUserId(commodityInfo.getSellerId());
            //这里orderId 和commodityId 一样是随机数，所以可以调用一样的方法。当时没做成until包  有点可惜
            int orderId =commodityService.getCommodityId();
            order.setId(orderId);
            if (orderService.generateOrder(order)){
                OrderDto orderDto = new OrderDto();
                BeanUtil.copyProperties(commodityInfo,orderDto);
                orderDto.setFirstPhoto(imageToBase64.ImageToBase64(orderDto.getFirstPhoto()));
                orderDto.setAddress(personInfoDto.getAddress());
                orderDto.setSellerName(userInfoMapper.getName(commodityInfo.getSellerId()));
                orderDto.setUserName(userInfoMapper.getName(userId));
                orderDto.setId(orderId);
                return orderDto;
            }else {
                throw  new ServiceException(Constants.CODE_500,"欲购买失败，请重新购买");
            }
    }
    @GetMapping("/payment")
    public  String payment(int orderId){
            Order order = orderService.queryOrderByOrderId(orderId);
            Date date = new Date();
            long minute = DateUtil.between(order.getSubmitTime(),date,DateUnit.DAY);
            if (minute>=1){
                order.setOrderStatus("超时了");
                orderMapper.updateByPrimaryKey(order);
                throw  new ServiceException(Constants.CODE_400,"订单超时");
            }
            order.setOrderStatus("已支付");
            orderMapper.updateByPrimaryKey(order);
            return  userLogMapper.selectByUserId(order.getSellUserId()).getEmail();

    }

    @GetMapping("/confirmDeliverGood")
    public  boolean confirmDeliverGood(int orderId){
        try {
            Order order = orderService.getById(orderId);
            order.setLogisticsStatus("已发货");
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",orderId);
            orderService.update(order,queryWrapper );
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"确认发货失败请重新确认");
        }
        return true;
    }

    @GetMapping("/confirmReceiveGood")
    public  boolean confirmReceiveGood(int orderId){
        try {
            Order order = orderService.getById(orderId);
            order.setLogisticsStatus("已收货");
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",orderId);
            orderService.update(order,queryWrapper );
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"确认收货失败请重新确认");
        }
        return  true;
    }

    @GetMapping("/collectAdd")
    public boolean insertCollect(@RequestParam Integer commodityId){
        int  userId = TokenUtils.getCurrentUser().getId();
        return collectService.shopInsertCollect(userId,commodityId);}

}

