package com.dental.clinic.module.captcha.controller;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.captcha.service.CaptchaSecurityService;
import com.dental.clinic.module.captcha.service.CaptchaService;
import com.dental.clinic.util.CaptchaMonitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "验证码接口", description = "验证码相关的接口")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService; // 注入验证码服务

    @Autowired
    private CaptchaSecurityService captchaSecurityService; // 注入安全服务

    @Autowired
    private CaptchaMonitor captchaMonitor; // 注入监控服务

    /**
     * 生成验证码（带频率限制）
     */
    @GetMapping("/captcha")
    @Operation(summary = "获取验证码", description = "生成captchaKey和captchaImage")
    public Result<Map<String, String>> generateCaptcha(HttpServletRequest request) {

        // 检查请求频率
        String clientIp = captchaSecurityService.getClientIp(request);
        if (captchaSecurityService.isRateLimited(clientIp)) {
            return Result.error("请求频率过高，请稍后再试");
        }

        try {
            Map<String, String> captchaInfo = captchaService.generateCaptcha();
            return Result.success("验证码生成成功", captchaInfo);
        } catch (Exception e) {
            return Result.error("验证码生成失败: " + e.getMessage());
        }
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify-captcha")
    @Operation(summary = "验证验证码", description = "验证验证码是否正确")
    public Result<Boolean> verifyCaptcha(@RequestParam String captchaKey, @RequestParam String captchaCode) {
        try {
            boolean isValid = captchaService.verifyCaptcha(captchaKey, captchaCode);
            if (isValid) {
                return Result.success("验证码正确", true);
            } else {
                return Result.error("验证码错误或已过期");
            }
        } catch (Exception e) {
            return Result.error("验证码验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取验证码统计信息（用于管理后台）
     */
    @GetMapping("/captcha/stats")
    @Operation(summary = "获取验证码统计信息", description = "获取验证码统计信息（用于管理后台）")
    public Result<CaptchaMonitor.CaptchaStats> getCaptchaStats() {
        CaptchaMonitor.CaptchaStats stats = captchaMonitor.getCaptchaStats();
        return Result.success("验证码统计信息", stats);
    }

    /**
     * 验证码监控信息接口
     */
    @GetMapping("/captcha/monitor")
    @Operation(summary = "验证码监控信息", description = "获取验证码监控信息")
    public Result<Map<String, Object>> getMonitorInfo() {
        CaptchaMonitor.CaptchaStats stats = captchaMonitor.getCaptchaStats();
        boolean isRisk = captchaMonitor.isMemoryRisk();

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);
        result.put("isMemoryRisk", isRisk);
        result.put("message", isRisk ? "存在内存风险" : "内存使用正常");

        return Result.success(result);
    }

    /**
     * 清理验证码接口（管理用）
     */
    @PostMapping("/captcha/cleanup")
    @Operation(summary = "清理验证码", description = "清理所有验证码（管理用）")
    public Result<Long> cleanupCaptchas() {
        long cleanedCount = captchaMonitor.cleanupAllCaptchas();
        return Result.success("清理完成", cleanedCount);
    }

}