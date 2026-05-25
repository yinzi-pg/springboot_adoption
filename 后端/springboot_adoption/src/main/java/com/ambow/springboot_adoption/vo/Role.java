// 角色表实体类
package com.ambow.springboot_adoption.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    // 角色ID（自增主键）
    private Integer roleId;

    // 角色名称（如：管理员、普通用户）
    private String roleName;

    // 角色编码（唯一标识，如：admin、user）
    private String roleCode;

    // 角色权限（JSON格式字符串，如：["user:manage", "pet:manage"]）
    private String permissions;

    // 角色描述
    private String roleDesc;

    // 角色创建时间
    // 格式：yyyy-MM-dd HH:mm:ss
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
}
