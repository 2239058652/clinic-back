package com.dental.clinic.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dental.clinic.common.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Result.error(401, "未授权，请先登录"));
        out.print(json);
        out.flush();
        out.close();
    }
}