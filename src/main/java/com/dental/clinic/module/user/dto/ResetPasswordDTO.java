package com.dental.clinic.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,11}$", message = "用户名格式不正确") // 简单校验
    private String username; // 要重置密码的用户名
}