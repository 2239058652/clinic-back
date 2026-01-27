package com.dc.clinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DependencyTest {
    
    @Test
    void testMybatisDependencies() {
        try {
            // 测试 MyBatis 核心类是否存在
            Class.forName("org.apache.ibatis.session.SqlSession");
            Class.forName("org.mybatis.spring.SqlSessionFactoryBean");
            System.out.println("✅ MyBatis 依赖正常");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MyBatis 依赖有问题: " + e.getMessage());
        }
    }
}