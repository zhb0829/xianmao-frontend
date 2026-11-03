package cn.edu.gxu.service.Imp;

import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.dao.OrderMapper;
import cn.edu.gxu.dao.UserOrderMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.Order;
import cn.edu.gxu.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements OrderService {
    @Resource
    OrderMapper orderMapper;
    @Resource
    private UserOrderMapper userOrderMapper;

    @Override
    public List<Order> selectUserID(Integer userId) {
        return userOrderMapper.selectUserID(userId);
    }

    @Override
    public Order queryOrderByOrderId(int orderId) {
        Order order = new Order();
        try {
            order = orderMapper.selectBy_Id(orderId);
        }catch (Exception e){
            log.error("系统出错了");
            throw  new ServiceException(Constants.CODE_500,"系统出错了");
        }
        return  order;
    }

    @Override
    public List<Order> selectSellUserID(Integer sellUserId) {return userOrderMapper . selectSellUserID(sellUserId);
    }

    public boolean generateOrder(Order order) {
        return orderMapper.insert(order)==1;
    }

    @Override
    public boolean ReceiveCommodity(Integer orderId) {
        Order order = new Order();
        order = orderMapper.selectBy_Id(orderId);
        if (order.getLogisticsStatus().equals("已发货")){
                order.setLogisticsStatus("确认收货");
                orderMapper.updateBy_Id(orderId);
                return true;
        }
        if(order.getLogisticsStatus().equals("未发货")){
            order.setLogisticsStatus("已发货");
            orderMapper.updateBy_Idd(orderId);
            return true;
        }
        else
            return false;
    }
}
