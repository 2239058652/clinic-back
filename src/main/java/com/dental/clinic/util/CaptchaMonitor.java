package com.dental.clinic.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableScheduling
public class CaptchaMonitor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 获取验证码统计信息
     */
    public CaptchaStats getCaptchaStats() {
        try {
            Set<String> keys = redisTemplate.keys(CAPTCHA_PREFIX + "*");
            long captchaCount = keys.size();
            long estimatedMemory = captchaCount * 100; // 每个约100字节

            return new CaptchaStats(captchaCount, estimatedMemory);
        } catch (Exception e) {
            System.err.println("获取验证码统计信息失败: " + e.getMessage());
            return new CaptchaStats(0, 0);
        }
    }

    /**
     * 获取验证码内存使用情况
     */
    public void monitorCaptchaMemory() {
        try {
            CaptchaStats stats = getCaptchaStats();

            // 只在数量较多时记录警告日志
            if (stats.captchaCount() > 100) {
                System.out.println("🚨 验证码数量较多: " + stats.captchaCount() + "个, 占用内存: " +
                        String.format("%.2f", stats.estimatedMemoryKB()) + " KB");
            } else {
                System.out.println("✅ 验证码监控 - 数量: " + stats.captchaCount() + "个, 内存: " +
                        stats.estimatedMemoryBytes() + " bytes");
            }

        } catch (Exception e) {
            System.err.println("验证码内存监控异常: " + e.getMessage());
        }
    }

    /**
     * 每5分钟执行一次内存监控
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void scheduledMemoryCheck() {
        System.out.println("=== 定时验证码内存检查 ===");
        monitorCaptchaMemory();
    }

    /**
     * 每小时执行一次详细统计
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void detailedMemoryReport() {
        CaptchaStats stats = getCaptchaStats();
        System.out.println("📊 验证码详细统计 - 数量: " + stats.captchaCount() + "个, 内存: " +
                String.format("%.2f", stats.estimatedMemoryKB()) + " KB (" +
                String.format("%.4f", stats.estimatedMemoryMB()) + " MB)");
    }

    /**
     * 检查是否存在内存风险
     */
    public boolean isMemoryRisk() {
        CaptchaStats stats = getCaptchaStats();
        // 如果超过1000个验证码或占用超过100KB，认为有风险
        return stats.captchaCount() > 1000 || stats.estimatedMemoryKB() > 100;
    }

    /**
     * 清理所有验证码（用于测试或管理）
     */
    public long cleanupAllCaptchas() {
        try {
            Set<String> keys = redisTemplate.keys(CAPTCHA_PREFIX + "*");
            long count = keys.size();
            if (count > 0) {
                redisTemplate.delete(keys);
                System.out.println("🧹 清理了 " + count + " 个验证码");
            }
            return count;
        } catch (Exception e) {
            System.err.println("清理验证码失败: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 验证码统计信息 - 使用 Record
     */
    public record CaptchaStats(long captchaCount, long estimatedMemoryBytes) {

        // 计算属性 - 注意：Record 的方法名不需要 "get" 前缀
        public double estimatedMemoryKB() {
            return estimatedMemoryBytes / 1024.0;
        }

        public double estimatedMemoryMB() {
            return estimatedMemoryBytes / (1024.0 * 1024.0);
        }

        // Record 会自动生成 toString()，但我们可以自定义
        @Override
        public String toString() {
            return String.format("CaptchaStats[count=%d, memory=%.2f KB]",
                    captchaCount, estimatedMemoryKB());
        }
    }
}