package cn.edu.gxu.controller;

import cn.edu.gxu.controller.dto.ManageCommodityDto;
import cn.edu.gxu.controller.dto.ManageDto;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.service.ManageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.Web.bind.annotation.GetMapping;
import org.springframework.Web.bind.annotation.RequestMapping;
import org.springframework.Web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage")
public class ManageController {

    @Resource
    ManageService manageService;
    @GetMapping("/select")
    public Page<ManageCommodityDto> getCommodity(ManageDto manageDto){
        System.out.println(manageDto.toString());
        return manageService.getCommodity(manageDto);
    }

    @GetMapping("/approved")
    public Boolean approvedCommodity(Integer commodityId){
        System.out.println("/approved:"+commodityId);
        return manageService.updateCommodity(commodityId,"已审核");
    }

    @GetMapping("/remove")
    public Boolean updateCommodity(Integer commodityId){
        System.out.println("/remove:"+commodityId);
        return manageService.updateCommodity(commodityId,"已下架");
    }

    @GetMapping("/category")
    public Map<String,Object> getCategory(){
        return manageService.getCategory();
    }

    @GetMapping("/deal")
    public Map<String,String[]> getDeal(){
        return manageService.getDeal();
    }

    @GetMapping("/statusNum")
    public  Map<String,String[]> getStatusNum(){return manageService.getStatus();}
}
