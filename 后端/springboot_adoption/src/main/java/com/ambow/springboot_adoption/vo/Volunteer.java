package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Volunteer {
    private Integer volunteerId;         // 志愿者唯一ID（主键）
    private Integer userId;              // 关联用户表的用户ID（外键）
    private String volunteerType;        // 志愿者服务类型
    //    private BigDecimal serviceHours;     // 累计服务时长（小时）
    private double serviceHours;
    private String volunteerStatus;      // 志愿者状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;    // 志愿者注册时间
    private String remark;               // 备注信息
    private String volunteerImage;
}
