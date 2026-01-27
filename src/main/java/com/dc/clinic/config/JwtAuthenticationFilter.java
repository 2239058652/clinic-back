package com.dc.clinic.config;

import com.dc.clinic.common.utils.JwtUtils;
import com.dc.clinic.modules.auth.dto.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 获取请求头中的 token
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        try {
            // 2. 解析 token 获取用户名
            String username = jwtUtils.getUsernameFromToken(token);

            // 3. 从 Redis 获取 LoginUser 对象 (注意：这里不再强转为 String)
            // 这里的 Key 要和你 AuthService 存入时保持一致，假设是 "login:token:" + username
            Object cacheObject = redisTemplate.opsForValue().get("login:token:" + username);

            if (cacheObject instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) cacheObject;

                // 4. 这里的 LoginUser 已经包含我们在登录时存入的 permissions 了
                // 必须传入 loginUser.getAuthorities()，Security 才会认可权限
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities());

                // 5. 设置到上下文
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                // 如果缓存里没拿到对象，说明 Token 可能在 Redis 里过期了或格式不对
                logger.error("无法从 Redis 获取用户信息，Token 可能已失效");
            }

        } catch (Exception e) {
            logger.error("JWT 校验失败: " + e.getMessage());
        }

        // 6. 放行
        filterChain.doFilter(request, response);
    }
}