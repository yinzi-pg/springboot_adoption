package com.ambow.springboot_adoption.service;
import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderItemService {

    //查全部
    //List<OrderItem> selectAllOrderItems();
    IPage<OrderItem> selectAllOrderItemPage(int pageNum, int pageSize);
    //根据userId查询订单详情
    List<OrderItem> getOrderItemsByUserId(Integer userId);
    //根据订单id查询订单详情
    List<OrderItem> getOrderByOrderId(Integer orderId);

    // 根据userId查询购物车
    IPage<OrderItem> selectAllOrderItemByUserIdPage(int pageNum, int pageSize, Integer userId, Integer status);


    //添加
    int addOrderItem(OrderItem orderItem);

    //删除
    int deleteOrderItem(Integer orderItemId);

    //修改
    int updateOrderItem(OrderItem orderItem);

    //模糊查询
    List<OrderItem> findOrderItemByKeyword(String keyword);
}
