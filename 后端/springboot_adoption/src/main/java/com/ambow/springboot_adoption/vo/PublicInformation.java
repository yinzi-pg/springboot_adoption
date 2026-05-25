// 公共信息表实体类
package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicInformation {
    // 信息ID（自增主键）
    private Integer publicId;

    // 信息标题
    private String title;

    // 信息内容
    private String content;

    // 发布作者（部门或个人）
    private String author;

    // 状态（如：已发布、草稿）
    private String status;

    // 发布时间（状态为已发布时有效）
    // 格式：yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;

    // 记录创建时间（信息草稿创建时间）
    // 格式：yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    private String publicImage;
}
