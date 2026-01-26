package com.dc.clinic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 诊所管理系统 - 主启动类
 * -
 * -@SpringBootApplication 是一个组合注解，包含：
 * - @Configuration: 标记为配置类
 * - @EnableAutoConfiguration: 启用自动配置
 * - @ComponentScan: 自动扫描组件
 */
@SpringBootApplication
@MapperScan("com.dc.clinic.modules.*.mapper") // 使用通配符 * 匹配中间的模块名，精确锁定到 mapper 包
public class ClinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicApplication.class, args);

		System.out.println("\n" +
				"╔═══════════════════════════════════════════════╗\n" +
				"║                                               ║\n" +
				"║   🏥 Clinic Backend Started Successfully! 🏥  ║\n" +
				"║                                               ║\n" +
				"║   🌐 Server: http://localhost:9095           ║\n" +
				"║   📚 Swagger: http://localhost:9095/swagger-ui/index.html#/ ║\n" +
				"║                                               ║\n" +
				"╚═══════════════════════════════════════════════╝\n");
	}
}