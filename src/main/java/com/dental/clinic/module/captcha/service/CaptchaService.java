package com.dental.clinic.module.captcha.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.dental.clinic.util.CaptchaMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CaptchaMonitor captchaMonitor; // 注入监控器

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_MINUTES = 2;

    /**
     * 生成验证码
     */
    public Map<String, String> generateCaptcha() {
        // 定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);

        // 获取验证码内容
        String code = lineCaptcha.getCode();
        // 获取Base64编码的图片
        String imageBase64 = lineCaptcha.getImageBase64();

        // 生成验证码key（使用UUID确保唯一性）
        String captchaKey = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // 存储到Redis，2分钟过期
        redisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + captchaKey,
                code,
                CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // ✅ 使用监控器：记录验证码生成
        System.out.println("生成验证码，Key: " + captchaKey);
        captchaMonitor.monitorCaptchaMemory(); // 每次生成时检查内存

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", "data:image/png;base64," + imageBase64);

        return result;
    }

    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String captchaKey, String inputCode) {
        if (captchaKey == null || inputCode == null) {
            return false;
        }

        String redisKey = CAPTCHA_PREFIX + captchaKey;
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            return false;
        }

        // 无论验证成功与否，都删除验证码（一次性使用）
        redisTemplate.delete(redisKey);

        // ✅ 使用监控器：验证后检查内存
        captchaMonitor.monitorCaptchaMemory();

        return storedCode.equalsIgnoreCase(inputCode);
    }

    /**
     * 手动清理所有验证码（用于管理）
     */
    public long cleanupAllCaptchas() {
        Set<String> keys = redisTemplate.keys(CAPTCHA_PREFIX + "*");
        if (!keys.isEmpty()) {
            long count = keys.size();
            redisTemplate.delete(keys);

            // ✅ 使用监控器：清理后检查
            captchaMonitor.monitorCaptchaMemory();
            System.out.println("清理了 " + count + " 个验证码");

            return count;
        }
        return 0;
    }
}