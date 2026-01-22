package com.dental.clinic.config;

import com.dental.clinic.security.JwtAuthenticationEntryPoint;
import com.dental.clinic.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态，不使用 Session
                .authorizeHttpRequests(authz -> authz
                        // --- 明确允许访问登录、注册、获取验证码信息、获取验证码图片接口 ---
                        .requestMatchers("/api/users/login", "/api/users/register", "/api/auth/**", "/api/files/local/avatar/**").permitAll()
                        // ----------------------------------------------------
                        // --- 明确允许访问管理员用户管理接口 (需要 ADMIN 角色) ---
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN") // 添加这一行
                        // --- 允许访问 Swagger UI 和 API 文档 ---
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // ----------------------------------------------------
                        // --- 允许访问 Actuator loggers 端点 ---
                        .requestMatchers("/actuator/loggers", "/actuator/loggers/**").permitAll()
                        .requestMatchers("/error").permitAll()  // 错误接口放行
                        // ----------------------------------------------------
                        .anyRequest().authenticated() // 其他所有请求都需要认证
                );

        // 添加 JWT 过滤器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}