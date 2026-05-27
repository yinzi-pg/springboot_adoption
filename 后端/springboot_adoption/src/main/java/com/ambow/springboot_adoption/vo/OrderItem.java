//宠物用品OrderItem
package com.ambow.springboot_adoption.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    // 订单项ID（自增主键）
    private Integer orderItemId;

    // 关联订单ID（外键关联order表的order_id）
    private Integer orderId;

//    // 商品ID（对应宠物用品ID）
//    private Integer productId;
//
//    // 商品名称
//    private String productName;

    // 商品ID（对应宠物用品ID）
    private Integer productId;

    // 商品名称
    private String productName;

    // 购买数量
    private Integer quantity;

    // 商品单价
    private BigDecimal price;

    // 新增字段：user_id
    private Integer userId;

    // 新增字段：status
    private Integer status;  // 0-购物车中，1-已下单，2-已移除


}
