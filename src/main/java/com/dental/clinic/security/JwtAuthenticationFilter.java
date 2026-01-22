package com.dental.clinic.security;

import com.dental.clinic.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;

        final String requestTokenHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
            try {
                // 【优化】安全地获取用户名，捕获过期等异常
                username = jwtUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) { // 【新增】捕获过期异常
                System.out.println("JWT Token has expired");
                // 【重要】Token 过期，直接设置响应状态并返回，不再继续执行后续过滤器链
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                // 可以写入更详细的错误信息
                String jsonResponse = "{\"code\": 401, \"message\": \"Token has expired\"}";
                response.getWriter().write(jsonResponse);
                return; // 终止请求处理
            }
            // 可以捕获其他 JWT 异常，如 SignatureException, MalformedJwtException 等
        } else {
            logger.debug("JWT Token does not begin with Bearer String");
        }

        // 一旦我们获得 token，验证它。
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 如果 token 有效，则设置认证信息
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                // 【可选】Token 无效，也可以选择返回 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\": 401, \"message\": \"Invalid Token\"}");
                logger.debug("Invalid JWT token.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}