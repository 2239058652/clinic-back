package com.dc.clinic.modules.auth.service;

import com.dc.clinic.common.response.Result;
import com.dc.clinic.common.utils.JwtUtils;
import com.dc.clinic.modules.auth.dto.LoginRequest;
import com.dc.clinic.modules.auth.dto.LoginUser;
import com.dc.clinic.modules.system.entity.User;
import com.dc.clinic.modules.system.mapper.PermissionMapper;
import com.dc.clinic.modules.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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

    @Autowired
    private PermissionMapper permissionMapper;

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

        // 2. 查询权限列表
        List<String> permCodes = permissionMapper.selectPermissionCodesByUserId(user.getId());

        // 3. 封装 LoginUser (带权限)
        LoginUser loginUser = new LoginUser(user, new HashSet<>(permCodes));

        // 4. 存入 Redis 并返回 Token
        // 注意：建议把 loginUser 序列化成 JSON 存入 Redis，这样 Filter 就不必每次都查库
        redisTemplate.opsForValue().set("login:token:" + user.getUsername(), loginUser, 24, TimeUnit.HOURS);

        String token = jwtUtils.createToken(user.getUsername());
        return Result.success(token);
    }
}