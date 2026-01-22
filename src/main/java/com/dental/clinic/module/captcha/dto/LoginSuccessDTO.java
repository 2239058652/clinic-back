package com.dental.clinic.module.captcha.dto;

import com.dental.clinic.module.user.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class LoginSuccessDTO {
    private String token;
    private User userInfo; // 包含用户信息
    private List<String> roles; // 【新增】用户的角色列表

    public LoginSuccessDTO(String token, User userInfo, List<String> roles) {
        this.token = token;
        this.userInfo = userInfo;
        this.roles = roles;
    }
}