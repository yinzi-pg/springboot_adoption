package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PetSupplies {
    private Integer supplyId;
    private String supplyName;
    private String supplyType;
    private String brand;
    private double price;
    private String status;
    private Integer stock;
    private String description;
    private String supplyImage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createDate;
}
