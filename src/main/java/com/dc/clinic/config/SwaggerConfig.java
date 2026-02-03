package com.dc.clinic.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI clinicOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏥 诊所管理系统 API 文档")
                        .description("后端接口文档，包含用户管理、权限校验等模块")
                        .version("v1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("JWT_TOKEN",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)))
                .security(Collections.singletonList(   // 使用 security() 方法
                        new SecurityRequirement().addList("JWT_TOKEN")
                ));
    }
}