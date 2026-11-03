package cn.edu.gxu.controller;

import cn.edu.gxu.model.Order;
import cn.edu.gxu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.Web.bind.annotation.GetMapping;
import org.springframework.Web.bind.annotation.RequestMapping;
import org.springframework.Web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/userOrder")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/user")
    public List<Order> selectUser(Integer userID){
        return orderService.selectUserID(userID);
    }

    @GetMapping("/sellUser")
    public List<Order> selectSellUser (Integer sellUserID){return orderService.selectSellUserID(sellUserID);}
}
