//领养记录表
package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Adoption {
    private Integer adoptionId;
    private String applicantName;
    private String applicantPhone;
    private Integer petId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    //申请时间
    private LocalDateTime applyDate;
    //状态
    private String status;
    //备注信息
    private String remark;
    //审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewDate;
}
