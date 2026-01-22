package com.dental.clinic.module.rolepermission.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Permission {
    private Long id;
    private String permissionName; // 如 "查看用户"
    private String permissionCode; // 如 "user:read"
    private String description;    // 描述
    private String resourceType;   // 资源类型 (可选)
    private String action;         // 操作类型 (可选)
    private Integer status; // 0-禁用, 1-启用
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer deleted; // 0-未删除, 1-已删除
}