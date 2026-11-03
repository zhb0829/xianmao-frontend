package cn.edu.gxu.service;


import cn.edu.gxu.model.Order;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface OrderService extends IService<Order> {

    java.util.List<Order> selectUserID(Integer userId);
    boolean generateOrder(Order order);
    List<Order> selectSellUserID(Integer sellUserId);

    Order queryOrderByOrderId(int orderId);
    boolean ReceiveCommodity(Integer orderId);
}
