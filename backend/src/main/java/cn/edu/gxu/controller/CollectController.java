package cn.edu.gxu.controller;


import cn.edu.gxu.model.CommodityInfo;
import cn.edu.gxu.service.CollectService;
import cn.edu.gxu.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.Web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    @GetMapping("/sel")
    public List<CommodityInfo> selectCollect(){
        int  userid = TokenUtils.getCurrentUser().getId();
        return collectService.selectCollect(userid);}

    @GetMapping("/del")
    public int deleteCollect(@RequestParam Integer commodityId){
        System.out.println(commodityId+"\n\n\n\n");
        int  userId = TokenUtils.getCurrentUser().getId();
        return collectService.deleteCollect(userId,commodityId);}

    @GetMapping("/add")
    public boolean insertCollect(@RequestParam Integer orderId){
        int  userId = TokenUtils.getCurrentUser().getId();
        return collectService.insertCollect(userId,orderId);}


}
