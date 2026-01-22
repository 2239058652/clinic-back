package com.dental.clinic.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status; // 0-禁用, 1-启用
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer deleted; // 0-未删除, 1-已删除
}