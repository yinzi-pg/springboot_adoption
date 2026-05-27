package com.ambow.springboot_adoption.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("order_item")
public class OrderItem {
    @TableId(value = "orderItem_id", type = IdType.AUTO)
    private Integer orderItemId;
    private Integer orderId;
    private Integer userId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private Integer status; // 0-购物车中，1-已下单，2-已移除
}
