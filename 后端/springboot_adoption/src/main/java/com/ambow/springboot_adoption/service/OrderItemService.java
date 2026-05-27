package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PageBean;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrderItemService extends IService<OrderItem> {
    PageBean<OrderItem> getOrderItemList(Integer pageNum, Integer pageSize);
    PageBean<OrderItem> getOrderByUserId(Integer userId, Integer status, Integer pageNum, Integer pageSize);
    List<OrderItem> getOrderByOrderId(String orderIdParam);
    void addOrderItemService(OrderItem orderItemModel);
    void updateOrderItemService(OrderItem orderItemModel);
    void deleteOrderItemService(Integer orderItemId);
    PageBean<OrderItem> searchAdoptionService(String keyword, Integer page, Integer pageSize);
}
