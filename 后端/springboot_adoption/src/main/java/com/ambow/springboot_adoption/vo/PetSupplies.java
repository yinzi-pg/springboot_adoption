// 宠物用品表实体类
package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetSupplies {
    // 用品ID（自增主键）
    private Integer supplyId;

    // 用品名称
    private String supplyName;

    // 用品类型（如：主粮、玩具、清洁用品）
    private String supplyType;

    // 品牌名称
    private String brand;

    // 用品单价
    private BigDecimal price;

    // 库存数量
    private Integer stock;

    // 状态（如：上架、下架）
    private String status;

    // 用品描述（材质、功能等）
    private String description;

    // 用品图片路径
    private String supplyImage;

    // 记录创建时间（用品录入系统时间）
    // 格式：yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
}
