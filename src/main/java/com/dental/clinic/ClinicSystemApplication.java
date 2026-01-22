package com.dental.clinic;

// import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy // 启用 AspectJ 代理
@EnableScheduling // 启用定时任务
public class ClinicSystemApplication {

	public static void main(String[] args) {

		// 加载 .env.development 文件
		// Dotenv.configure()
		// .filename(".env.development") // 指定文件名
		// .ignoreIfMissing() // 文件不存在不报错
		// .systemProperties() // 这一行代替了手动 forEach
		// .load();

		// 注入到系统属性
		// dotenv.entries().forEach(entry ->
		// System.setProperty(entry.getKey(), entry.getValue())
		// );

		SpringApplication.run(ClinicSystemApplication.class, args);
	}

}
