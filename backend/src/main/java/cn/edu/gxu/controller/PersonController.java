package cn.edu.gxu.controller;

import cn.edu.gxu.controller.dto.PersonDetailsDto;
import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.service.CommodityService;
import cn.edu.gxu.service.OrderService;
import cn.edu.gxu.service.PersonInfoService;
import cn.edu.gxu.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.Web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonInfoService personInfoService;
    @Autowired
    private OrderService orderService;


    @GetMapping("/details")
    public Map<String,Object> getPersonNumber(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return personInfoService.getPersonNumber(userid);
    }

    @GetMapping("/liked")
    public List<CommodityInfo> getLiked(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return personInfoService.getLiked(userid);
    }

    @GetMapping("/bought")
    public List<PersonDetailsDto> getBought(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return personInfoService.getBought(userid);
    }

    @GetMapping("/sold")
    public List<PersonDetailsDto> getSold(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return personInfoService.getSold(userid);
    }


    @GetMapping("/published")
    public List<CommodityInfo> getPublished(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return personInfoService.getPublished(userid);
    }

    @GetMapping("/receive")
    public boolean getReceive(Integer orderId){
        return orderService.ReceiveCommodity(orderId);
    }
}
