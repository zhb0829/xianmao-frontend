package cn.edu.gxu.service;

import cn.edu.gxu.controller.dto.ManageCommodityDto;
import cn.edu.gxu.controller.dto.ManageDto;
import cn.edu.gxu.model.CommodityInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/*
查看全部未审核商品 （查） 更改商品审核状态
 */
@Service
public interface ManageService {

    Page<ManageCommodityDto> getCommodity(ManageDto manageDto);

    boolean updateCommodity(int CommodityId,String status);

    Map<String,Object> getCategory();

    Map<String,String[]> getDeal();
    Map<String,String[]> getStatus();
}
