package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Order;
import com.ambow.springboot_adoption.vo.PageBean;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface OrderService extends IService<Order> {
    PageBean<Order> getOrderList(Integer pageNum, Integer pageSize);
    List<Order> selectOrderById(Integer userId);
    void orderAddService(Order orderModel);
    void orderDeleteService(Integer orderId);
    void orderUpdateService(Order orderModel);
    PageBean<Order> searchOrderService(String keyword, Integer page, Integer pageSize);
}
