package com.dental.clinic.module.captcha.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha; // 用户输入的验证码字符

    @NotBlank(message = "验证码Key不能为空")
    private String captchaKey; // 从前端获取的验证码唯一标识 (对应你的 CaptchaService 中的 captchaKey)
}