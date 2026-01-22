package com.dental.clinic.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 标记这是一个配置类
public class OssConfig {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    /**
     * 创建 OSS 客户端 Bean
     * @return OSS 客户端实例
     */
    @Bean // 标记这个方法会返回一个 Bean
    public OSS ossClient() {
        // 使用配置文件中的信息创建 OSSClient
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}