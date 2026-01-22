package com.dental.clinic.module.captcha.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaSecurityService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_RATE_LIMIT_PREFIX = "captcha_rate:";
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final Logger logger = LoggerFactory.getLogger(CaptchaSecurityService.class);

    /**
     * 检查验证码请求频率 - 添加异常处理
     */
    public boolean isRateLimited(String clientIp) {
        try {
            String rateKey = CAPTCHA_RATE_LIMIT_PREFIX + clientIp;

            Long requestCount = redisTemplate.opsForValue().increment(rateKey);

            if (requestCount != null && requestCount == 1) {
                redisTemplate.expire(rateKey, 1, TimeUnit.MINUTES);
            }

            return requestCount != null && requestCount > MAX_REQUESTS_PER_MINUTE;

        } catch (Exception e) {
            logger.error("频率限制检查失败", e);
            return false;
        }
    }

    /**
     * 获取客户端IP - 添加空值检查
     */
    public String getClientIp(jakarta.servlet.http.HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
            ip = request.getHeader("X-Real-IP");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            logger.error("获取客户端IP失败", e);
            return "unknown";
        }
    }
}