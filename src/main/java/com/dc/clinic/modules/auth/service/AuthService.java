package com.dc.clinic.modules.auth.service;

import com.dc.clinic.common.response.Result;
import com.dc.clinic.common.utils.JwtUtils;
import com.dc.clinic.modules.auth.dto.LoginRequest;
import com.dc.clinic.modules.system.entity.User;
import com.dc.clinic.modules.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Result<String> login(LoginRequest request) {
        // 1. 查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        // 2. 校验
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        
        if (!"ACTIVE".equals(user.getStatus())) {
            return Result.error("账号已被禁用");
        }

        // 3. 生成 Token
        String token = jwtUtils.createToken(user.getUsername());

        // 4. 存入 Redis (实现 SSO 的关键：key中包含用户名)
        // 这样新登录会覆盖旧登录，或者你可以用来校验有效性
        redisTemplate.opsForValue().set("login:token:" + user.getUsername(), token, 24, TimeUnit.HOURS);

        return Result.success(token);
    }
}