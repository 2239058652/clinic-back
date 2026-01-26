package com.dc.clinic.modules.auth.controller;

import com.dc.clinic.common.response.Result;
import com.dc.clinic.modules.auth.dto.LoginRequest;
import com.dc.clinic.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录接口
     * - @Valid 注解会自动校验 LoginRequest 里的 @NotBlank 规则
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 调用 Service 层处理逻辑，成功则返回 Token
        return authService.login(loginRequest);
    }
}