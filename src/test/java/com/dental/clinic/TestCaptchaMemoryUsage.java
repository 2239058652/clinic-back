package com.dental.clinic;

import com.dental.clinic.module.captcha.service.CaptchaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TestCaptchaMemoryUsage {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试单线程生成验证码的内存使用
     */
    @Test
    void testSingleThreadCaptchaMemory() throws InterruptedException {
        System.out.println("=== 开始单线程验证码内存测试 ===");

        int generateCount = 1000;

        // 生成前的初始状态
        Set<String> initialKeys = redisTemplate.keys("captcha:*");
        System.out.println("初始验证码数量: " + initialKeys.size());

        // 生成验证码
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < generateCount; i++) {
            captchaService.generateCaptcha();
            if ((i + 1) % 100 == 0) {
                System.out.println("已生成 " + (i + 1) + " 个验证码");
            }
        }
        long endTime = System.currentTimeMillis();

        // 立即检查
        Set<String> keys = redisTemplate.keys("captcha:*");
        System.out.println("生成后验证码数量: " + keys.size());
        System.out.println("生成 " + generateCount + " 个验证码耗时: " + (endTime - startTime) + "ms");

        // 等待2分钟过期
        System.out.println("等待验证码过期 (2分钟)...");
        for (int i = 0; i < 12; i++) {
            Thread.sleep(10000); // 每10秒检查一次
            Set<String> currentKeys = redisTemplate.keys("captcha:*");
            System.out.println("等待 " + (i + 1) * 10 + " 秒后验证码数量: " +
                    currentKeys.size());
        }

        // 最终检查
        Set<String> finalKeys = redisTemplate.keys("captcha:*");
        System.out.println("2分钟后验证码数量: " + finalKeys.size());
        System.out.println("=== 单线程测试完成 ===\n");
    }

    /**
     * 测试并发生成验证码
     */
    @Test
    void testConcurrentCaptchaMemory() throws InterruptedException {
        System.out.println("=== 开始并发验证码内存测试 ===");

        int threadCount = 50; // 并发线程数
        int captchaPerThread = 20; // 每个线程生成的验证码数
        int totalCount = threadCount * captchaPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalCount);

        // 初始状态
        Set<String> initialKeys = redisTemplate.keys("captcha:*");
        System.out.println("初始验证码数量: " + initialKeys.size());

        // 并发生成验证码
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < captchaPerThread; j++) {
                        captchaService.generateCaptcha();
                        latch.countDown();
                    }
                } catch (Exception e) {
                    System.err.println("线程 " + threadId + " 执行失败: " + e.getMessage());
                }
            });
        }

        // 等待所有任务完成
        latch.await(2, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();

        // 检查结果
        Set<String> keys = redisTemplate.keys("captcha:*");
        System.out.println("并发生成后验证码数量: " + keys.size());
        System.out.println("并发生成 " + totalCount + " 个验证码耗时: " + (endTime - startTime) + "ms");

        executor.shutdown();
        System.out.println("=== 并发测试完成 ===\n");
    }

    /**
     * 测试验证码过期机制
     */
    @Test
    void testCaptchaExpiration() throws InterruptedException {
        System.out.println("=== 开始验证码过期测试 ===");

        // 生成少量验证码进行精确测试
        int testCount = 10;
        for (int i = 0; i < testCount; i++) {
            captchaService.generateCaptcha();
        }

        Set<String> keys = redisTemplate.keys("captcha:*");
        System.out.println("生成的验证码数量: " + keys.size());

        // 实时监控过期过程
        for (int minute = 0; minute <= 3; minute++) {
            Set<String> currentKeys = redisTemplate.keys("captcha:*");
            int currentCount = currentKeys.size();
            System.out.println(minute + " 分钟后验证码数量: " + currentCount);

            if (minute < 3) {
                Thread.sleep(60000); // 等待1分钟
            }
        }

        System.out.println("=== 过期测试完成 ===\n");
    }

    /**
     * 性能压力测试
     */
    @Test
    void testCaptchaPerformance() throws InterruptedException {
        System.out.println("=== 开始性能压力测试 ===");

        int[] testSizes = {100, 500, 1000, 2000};

        for (int size : testSizes) {
            System.out.println("\n--- 测试规模: " + size + " 个验证码 ---");

            // 清理之前的验证码
            cleanupCaptcha();

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                captchaService.generateCaptcha();
            }
            long endTime = System.currentTimeMillis();

            Set<String> keys = redisTemplate.keys("captcha:*");
            int actualCount = keys.size();

            System.out.println("预期生成: " + size + " 个");
            System.out.println("实际生成: " + actualCount + " 个");
            System.out.println("生成耗时: " + (endTime - startTime) + "ms");
            System.out.println("平均每个: " + ((endTime - startTime) / (double) size) + "ms");

            // 等待一会儿再进行下一轮测试
            Thread.sleep(2000);
        }

        System.out.println("=== 性能测试完成 ===\n");
    }

    /**
     * 清理所有验证码
     */
    @Test
    public void cleanupCaptcha() {
        Set<String> keys = redisTemplate.keys("captcha:*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("清理了 " + keys.size() + " 个验证码");
        }
    }
}