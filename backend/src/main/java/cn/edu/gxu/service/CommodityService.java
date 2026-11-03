package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.AllCommodityInfoDto;

import cn.edu.gxu.model.CommodityInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public interface CommodityService extends IService<CommodityInfo> {

    LinkedList<CommodityInfo> queryMoHu(String  tradeName);
    LinkedList<CommodityInfo>  queryCommodityByPrice(double lowPrice,double highPrice);
    LinkedList<CommodityInfo> queryCommodityByTime();
    public Page<AllCommodityInfoDto> SelectAll(int currentPage, int pageSize, String category,String tradeName,String status,Integer timeFlag,Integer priceFlag);
   List<Map<String,Object>> SelectByID(Integer id);
    Float selectPrice(Integer GoodsID);
   boolean launchCommodityInfo (CommodityInfo commodityInfo);
    int getCommodityId();
    boolean deleteCommodity(int commodityID);

}
