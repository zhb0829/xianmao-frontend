package cn.edu.gxu.service.Imp;
import cn.edu.gxu.Basic.Constants;
import cn.edu.gxu.controller.dto.ManageCommodityDto;
import cn.edu.gxu.controller.dto.ManageDto;
import cn.edu.gxu.dao.CommodityInfoMapper;
import cn.edu.gxu.dao.OrderMapper;
import cn.edu.gxu.dao.PersonInfoMapper;
import cn.edu.gxu.dao.UserInfoMapper;
import cn.edu.gxu.exception.ServiceException;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.service.ImageToBase64;
import cn.edu.gxu.service.ManageService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


@Transactional
@Service
public class ManageServiceImp extends ServiceImpl<CommodityInfoMapper,CommodityInfo> implements ManageService {
    @Resource
    CommodityInfoMapper commodityInfoMapper;
    @Resource
    ImageToBase64 imageToBase64;
    @Resource
    PersonInfoMapper personInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    OrderMapper orderMapper;
    @Override
    public Page<ManageCommodityDto> getCommodity(ManageDto manageDto){
        try{
            Page<ManageCommodityDto> allCommodityInfoPage = new Page<>();
            Page<CommodityInfo> page = new Page<>(manageDto.getPageNum(),manageDto.getPageSize());
            QueryWrapper<CommodityInfo>  queryWrapper = new QueryWrapper<>();
            if (manageDto.getOrderBy() == "ASC")
            {queryWrapper.orderByAsc("Category");}
            else {queryWrapper.orderByDesc("Category");}
            if (manageDto.getInput()!="")queryWrapper.like("TradeName",manageDto.getInput());
            if (manageDto.getCategory()!="")queryWrapper.eq("Category",manageDto.getCategory());
            if (manageDto.getStatus()!="")queryWrapper.eq("Status",manageDto.getStatus());
            List<CommodityInfo> allCommodity = commodityInfoMapper.selectPage(page,queryWrapper).getRecords();
            List<ManageCommodityDto> allCommodityInfo= new LinkedList<>();
            for (int i = 0; i < allCommodity.size(); i++) {
                ManageCommodityDto manageCommodityDto = new ManageCommodityDto();
                BeanUtil.copyProperties(allCommodity.get(i),manageCommodityDto,true);
                String fistPhoto =imageToBase64.ImageToBase64(manageCommodityDto.getFirstPhoto());
                manageCommodityDto.setId(allCommodity.get(i).getId());
                manageCommodityDto.setFirstPhoto(fistPhoto);
                manageCommodityDto.setSellerName(userInfoMapper.getName(manageCommodityDto.getSellerId()));
                allCommodityInfo.add(i,manageCommodityDto);
            }
            allCommodityInfoPage.setRecords(allCommodityInfo);
            allCommodityInfoPage.setTotal(page.getTotal());
            return allCommodityInfoPage;

        }catch (Exception e){
            log.error(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"查找商品失败");
        }
    }

    @Override
    public boolean updateCommodity(int CommodityId,String status) {
        try{
            CommodityInfo commodityInfo;
            QueryWrapper<CommodityInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ID",CommodityId);
            commodityInfo = getOne(queryWrapper);
            commodityInfo.setStatus(status);
            update(commodityInfo,queryWrapper);
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw  new ServiceException(Constants.CODE_500,"修改审核失败");
        }
    }

    @Override
    public Map<String, Object> getCategory() {
        LinkedList<String> category = new LinkedList<>();
        category = personInfoMapper.selectAllCategory();
        Map<String,Object> map  = new HashMap<>();
        LinkedList<Float> num = new LinkedList<>();
        float total = 0;
        for (int i=0;i<category.size();i++){
            num.add(commodityInfoMapper.getCategoryNum(category.get(i)));
            total = total + num.get(i);
        }
        for (int i=0;i<category.size();i++) {
            float temp = num.get(i);
            num.set(i,(temp/total));
            map.put(category.get(i),num.get(i));
        }
        return map;
    }

    @Override
    public Map<String, String[]> getDeal() {
        Date tempDate = new Date();
        long min = tempDate.getTime()-(7*86400000);
        tempDate.setTime(min);
        int tempNum = orderMapper.getDateNum(tempDate);
        Map<String, String[]> map = new LinkedHashMap<>();
        String[] num = new String[7];
        String[] date = new String[7];
        for (int i=0;i<7;i++){
            min=min+86400000;
            tempDate.setTime(min);
            num[i] = Integer.toString(orderMapper.getDateNum(tempDate) - tempNum);
            tempNum = orderMapper.getDateNum(tempDate) - tempNum;
            SimpleDateFormat ft = new SimpleDateFormat ("MM-dd");
            date[i] = ft.format(tempDate);
        }
        map.put("num",num);
        map.put("name",date);
        return map;
    }
    @Override
    public Map<String, String[]> getStatus() {
        LinkedHashMap<String, String[]> map = new LinkedHashMap<>();
        String[] num = new String[3];
        String[] data = new String[3];
        num[0] = Integer.toString(commodityInfoMapper.getStatusNum("已审核"));
        data[0] = "已审核";
        num[2] = Integer.toString(commodityInfoMapper.getStatusNum("已下架"));
        data[2] = "已下架";
        num[1] = Integer.toString(commodityInfoMapper.getStatusNum("未审核"));
        data[1] = "未审核";
        map.put("num",num);
        map.put("name",data);
        return map;
    }
}
