package com.dc.clinic.config;

import com.dc.clinic.common.utils.JwtUtils;
import com.dc.clinic.modules.auth.dto.LoginUser;
import com.dc.clinic.modules.system.entity.User;
import com.dc.clinic.modules.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // 1. 获取请求头中的 token
        String token = request.getHeader("Authorization");
        
        // 如果没有 token 或格式不对（不是以 Bearer 开头），直接放行
        // 注意：放行不代表通过，后续的 Security 拦截器会判断该资源是否需要权限
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 截取真正的 token 字符串
        token = token.substring(7);

        try {
            // 3. 解析 token 获取用户名
            String username = jwtUtils.getUsernameFromToken(token);

            // 4. 从 Redis 获取 token 进行校验（防止用户注销后 token 依然有效）
            String redisToken = (String) redisTemplate.opsForValue().get("login:token:" + username);
            if (!token.equals(redisToken)) {
                throw new RuntimeException("Token 已失效，请重新登录");
            }

            // 5. 封装 Authentication 对象存入 SecurityContextHolder
            // 这样后续的 Controller 就能通过注入获取当前用户信息了
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            LoginUser loginUser = new LoginUser(user);
            
            UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(loginUser, null, null);
            
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            // 解析失败（过期、伪造等）
            logger.error("JWT 校验失败: " + e.getMessage());
        }

        // 6. 放行
        filterChain.doFilter(request, response);
    }
}