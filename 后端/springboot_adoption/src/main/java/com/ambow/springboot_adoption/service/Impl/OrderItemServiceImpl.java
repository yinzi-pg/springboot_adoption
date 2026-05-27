package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.OrderItemMapper;
import com.ambow.springboot_adoption.service.OrderItemService;
import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    //查全部
//    public List<OrderItem> selectAllOrderItems(){
//
//        return orderItemMapper.selectAllOrderItem();
//    }
    public IPage<OrderItem> selectAllOrderItemPage(int pageNum, int pageSize){
        Page<OrderItem> page = new Page<>(pageNum, pageSize);
        return orderItemMapper.selectAllOrderItem(page);
    }
    //根据userId查询订单详情
    public List<OrderItem> getOrderItemsByUserId(Integer userId){
        return orderItemMapper.getOrderByOrderId(userId);
    }

    // 根据userId查询购物车
//    public IPage<OrderItem> selectAllOrderItemByUserIdPage(int pageNum, int pageSize, Integer userId){
//        Page<OrderItem> page = new Page<>(pageNum, pageSize);
//        return orderItemMapper.selectAllOrderItemByUserIdPage(page, userId);
//    }
    public IPage<OrderItem> selectAllOrderItemByUserIdPage(int pageNum, int pageSize, Integer userId, Integer status){
        Page<OrderItem> page = new Page<>(pageNum, pageSize);
        return orderItemMapper.selectAllOrderItemByUserIdPage(page, userId, status);
    }

    //根据订单id查询订单详情
    public List<OrderItem> getOrderByOrderId(Integer orderId){
        return orderItemMapper.getOrderByOrderId(orderId);
    }

    //添加
    public int addOrderItem(OrderItem orderItem){

        return orderItemMapper.addOrderItem(orderItem);
    }

    //删除
    public int deleteOrderItem(Integer orderItemId){
        return orderItemMapper.deleteOrderItem(orderItemId);
    }

    //修改
   public int updateOrderItem(OrderItem orderItem){
        return orderItemMapper.updateOrderItem(orderItem);
   }

    //模糊查询
    public List<OrderItem> findOrderItemByKeyword(String keyword){
        return orderItemMapper.findOrderItemByKeyword(keyword);
    }
}
