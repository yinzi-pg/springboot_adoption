// 宠物报失/发现表实体类
package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetReport {
    // 报告ID（自增主键）
    private Integer reportId;

    // 宠物名称
    private String petName;

    // 宠物类型（如：猫类、犬类）
    private String petType;

    // 宠物性别（如：公、母）
    private String petGender;

    // 宠物年龄（单位：岁，可为空）
    private Integer petAge;

    // 发现地点
    private String findLocation;

    // 联系人信息（电话）
    private String contactInfo;

    // 报告描述（宠物特征、发现情况等）
    private String reportDesc;

    // 报告图片路径（宠物照片）
    private String reportImage;

    // 报告提交时间
    // 格式：yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
}
