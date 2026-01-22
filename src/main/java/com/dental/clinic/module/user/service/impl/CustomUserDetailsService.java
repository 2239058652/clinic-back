package com.dental.clinic.module.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dental.clinic.module.user.mapper.UserMapper;
import com.dental.clinic.module.user.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义用户详情服务实现类
 * 实现了 Spring Security 的 UserDetailsService 接口
 * 用于提供用户认证所需的信息
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper; // 用户数据访问层

    /**
     * 根据用户名加载用户信息
     * 
     * @param username 用户名
     * @return UserDetails 包含用户信息和权限的 UserDetails 对象
     * @throws UsernameNotFoundException 当用户不存在时抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户信息 使用查询未删除用户的方法
        User user = userMapper.selectByUsernameAndNotDeleted(username);

        log.info("UserDetailsService: 查询用户信息: {}", user);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 从数据库查询用户的所有角色和权限
        List<String> roles = userMapper.selectUserRoles(user.getId());
        List<String> permissions = userMapper.selectUserPermissions(user.getId());

        log.info("UserDetailsService: 用户 {} 的角色: {}, 权限: {}", username, roles, permissions);

        // 将角色和权限转换为 Spring Security 的 GrantedAuthority 格式
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 添加角色权限 (Spring Security 规定角色权限必须以 "ROLE_" 开头)
        for (String roleCode : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
        }

        // 添加普通权限
        for (String permissionCode : permissions) {
            authorities.add(new SimpleGrantedAuthority(permissionCode));
        }

        // 注意：这里的 password 是从数据库查询出来的，必须是 BCrypt 加密后的格式
        // Spring Security 会自动使用 PasswordEncoder 来比对
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // 这里必须是加密后的密码
                authorities // 使用新的权限列表
        );
    }
}