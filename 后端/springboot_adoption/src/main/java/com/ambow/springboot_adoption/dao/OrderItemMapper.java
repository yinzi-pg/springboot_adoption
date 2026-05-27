package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.ambow.springboot_adoption.vo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    // 查询全部
    @Select("select * from order_item")
    //List<OrderItem> selectAllOrderItem();
    IPage<OrderItem> selectAllOrderItem(IPage<OrderItem> page);

    //根据订单id查询订单表中的信息
    @Select("select * from order_item where order_id = #{orderId}")
    List<OrderItem> getOrderByOrderId(Integer orderId);

    // 根据userId查询购物车
    @Select("select * from order_item where user_id = #{userId} and status = #{status}")
    IPage<OrderItem> selectAllOrderItemByUserIdPage(
            IPage<OrderItem> page,
            @Param("userId") Integer userId,
            @Param("status") Integer status); // 新增status参数


    // 新增（无时间字段，保持原有）
    @Insert("INSERT INTO order_item (\n" +
            "  order_id, product_id, product_name, quantity, price,user_id,status\n" +
            ") VALUES (\n" +
            "  #{orderId}, #{productId}, #{productName}, #{quantity}, #{price},#{userId},#{status}\n" +
            ")")
    int addOrderItem(OrderItem orderItem);

    // 删除
    @Delete("DELETE FROM order_item WHERE orderItem_id = #{orderItemId}")
    int deleteOrderItem(Integer OrderItemId);

    // 修改
    @Update("UPDATE order_item SET \n" +
            "  order_id = #{orderId},\n" +
            "  product_id = #{productId},\n" +
            "  product_name = #{productName},\n" +
            "  quantity = #{quantity},\n" +
            "  price = #{price},\n" +
            "  user_id = #{userId},\n" +
            "  status = #{status}\n" +
            "WHERE orderItem_id = #{orderItemId}")
    int updateOrderItem(OrderItem orderItem);

    // 模糊查询
    @Select("SELECT \n" +
            "  orderItem_id AS orderItemId,\n" +
            "  order_id AS orderId,\n" +
            "  product_id AS productId,\n" +
            "  product_name AS productName,\n" +
            "  quantity,\n" +
            "  price\n" +
            "FROM order_item \n" +
            "WHERE \n" +
            "  product_name LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  order_id LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  product_id LIKE CONCAT('%', #{keyword}, '%') or" +
            " orderItem_id LIKE CONCAT('%', #{keyword}, '%')")
    List<OrderItem> findOrderItemByKeyword(String keyword);

}
