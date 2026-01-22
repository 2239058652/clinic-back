package com.dental.clinic.module.rolepermission.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Role {
    private Long id;
    private String roleName;
    private String roleCode; // 用于权限系统，如 ROLE_DOCTOR
    private String description;
    private Integer status; // 0-禁用, 1-启用
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer deleted; // 0-未删除, 1-已删除
}