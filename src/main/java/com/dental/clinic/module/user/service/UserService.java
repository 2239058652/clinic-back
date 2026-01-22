package com.dental.clinic.module.user.service;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.captcha.dto.LoginSuccessDTO;
import com.dental.clinic.module.file.dto.UpdateProfileDTO;
import com.dental.clinic.module.rolepermission.dto.UserInfoWithRolesDTO;
import com.dental.clinic.module.user.dto.UserListDTO;
import com.dental.clinic.module.user.dto.UserQueryRequest;
import com.dental.clinic.module.user.entity.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {
    // 登录方法需要添加验证码参数
    Result<LoginSuccessDTO> login(String username, String password, String captchaCode, String captchaKey);

    // 注册方法
    Result<User> register(User user);

    // 获取用户信息(不包含角色)
    Result<User> getUserInfo(Long userId);


    // 【新增】获取用户信息（包含角色）
    Result<UserInfoWithRolesDTO> getUserInfoWithRoles(Long userId);

    // 更新用户信息
    Result<String> updateProfile(Long userId, UpdateProfileDTO updateProfileDTO);

    // 更新头像
    Result<String> updateAvatar(Long userId, String avatarUrl);

    // 根据用户名获取用户
    User getUserByUsername(String username);

    // 新增用户管理方法
    Result<User> createUser(User user);
    Result<User> getUserById(Long userId);

    // 获取用户列表，不返回包含角色信息
    Result<PageInfo<User>> listUsers(UserQueryRequest query); // 使用 PageInfo 封装分页结果

    // 获取用户列表，返回包含角色信息的 DTO
    Result<PageInfo<UserListDTO>> listUsersWithRoles(UserQueryRequest query);

    Result<String> updateUser(User user);
    Result<String> deleteUser(Long userId);
    Result<String> changeUserStatus(Long userId, Integer status);
    // 简单查询所有用户（用于 AdminController 中的 listUsers 示例）
    Result<List<User>> listAllUsers();

    // 为用户分配角色
    Result<String> assignRoleToUser(Long userId, Long roleId);

    // 从用户移除角色
    Result<String> removeRoleFromUser(Long userId, Long roleId);

    // 验证角色编码是否有效
    boolean isValidRoleCode(String roleCode);

    // 重置用户密码
    Result<String> resetUserPassword(String username);
}