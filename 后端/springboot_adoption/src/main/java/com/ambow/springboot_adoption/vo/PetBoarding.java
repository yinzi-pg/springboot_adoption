package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PetBoarding {
    private Integer boardingId;
    private Integer userPetId;
    private Integer userId;
    private String petName;
    private String petImage;
    private String userName;
    private String userPhone;
    @JsonFormat(pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date boardingStart;
    @JsonFormat(pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date boardingEnd;
    private  String boardingStatus;
    private String specialInstructions;
    private BigDecimal fee;
    private String paymentStatus;
    @JsonFormat(pattern = "yyyy-MM-yy HH:mm:ss" ,timezone = "GMT+8")
    private LocalDateTime createTime;
}
