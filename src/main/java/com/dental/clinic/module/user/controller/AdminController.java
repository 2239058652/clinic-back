package com.dental.clinic.module.user.controller;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.user.dto.*;
import com.dental.clinic.module.user.entity.User;
import com.dental.clinic.module.user.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // 用于权限控制
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users") // 管理员接口前缀
@Tag(name = "管理员接口", description = "用户管理相关的接口（需要管理员权限）")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "管理员创建新用户")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色 - 现在对应 roles 表中的 role_code='ADMIN'
    public Result<User> createUser(@RequestBody @Valid CreateUserDTO createUserDTO) {
        // 将 DTO 转换为 User 实体，并调用 Service
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(createUserDTO.getPassword()); // Service 层会加密
        user.setRealName(createUserDTO.getRealName());
        user.setPhone(createUserDTO.getPhone());
        user.setEmail(createUserDTO.getEmail());
        // user.setRole(createUserDTO.getRole()); // 删除这行！
        user.setStatus(createUserDTO.getStatus()); // 默认启用
        user.setDeleted(0);

        // 【重要】如果需要在创建用户的同时分配角色，需要在 userService.createUser 中调用分配角色的逻辑
        // 或者，创建一个独立的接口来分配角色，如 assignUserRole
        return userService.createUser(user);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list/no-role")
    @Operation(summary = "分页查询用户列表", description = "分页查询所有用户列表")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<PageInfo<User>> listUsersNoRole(UserQueryRequest query) {
        // 注意：UserQueryRequest 中的 role 参数也需要在 Controller 和 Service 中处理
        // 但由于我们移除了 users 表的 role 字段，这个查询条件需要调整或移除
        // 你可以保留 status 等其他查询条件
        return userService.listUsers(query);
    }

    /**
     * 分页查询用户列表 (包含角色信息)
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表", description = "分页查询所有用户列表，包含角色信息")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<PageInfo<UserListDTO>> listUsers(UserQueryRequest query) { // 修改返回类型
        // 调用修改后的服务方法
        return userService.listUsersWithRoles(query); // 修改方法调用
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/all")
    @Operation(summary = "查询用户列表", description = "查询所有用户列表")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<List<User>> listAllUsers() {
        // 调用 Service 的查询方法
        return userService.listAllUsers();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<String> updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        // 将 DTO 转换为 User 实体，并调用 Service
        User user = new User();
        user.setId(userId); // 设置ID用于更新
        user.setRealName(updateUserDTO.getRealName());
        user.setPhone(updateUserDTO.getPhone());
        user.setEmail(updateUserDTO.getEmail());
        // user.setRole(updateUserDTO.getRole()); // 删除这行！角色需要通过专门的接口管理
        user.setStatus(updateUserDTO.getStatus());
        // 注意：这里通常不直接更新密码，除非是重置密码的特殊接口

        return userService.updateUser(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<String> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{userId}/disable")
    @Operation(summary = "禁用用户", description = "根据用户ID禁用用户")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<String> disableUser(@PathVariable Long userId) {
        return userService.changeUserStatus(userId, 0); // 0 表示禁用
    }

    /**
     * 启用用户
     */
    @PutMapping("/{userId}/enable")
    @Operation(summary = "启用用户", description = "根据用户ID启用用户")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<String> enableUser(@PathVariable Long userId) {
        return userService.changeUserStatus(userId, 1); // 1 表示启用
    }

    // 管理员为用户分配角色
    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "为用户分配角色", description = "管理员为用户分配指定角色")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<String> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        return userService.assignRoleToUser(userId, roleId);   // 调用服务层方法
    }

    // 管理员取消用户角色
    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "取消用户角色", description = "管理员取消用户指定角色")
    @PreAuthorize("hasRole('ADMIN')") // 要求用户具有 ADMIN 角色
    public Result<String> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        // 调用服务层方法
        return userService.removeRoleFromUser(userId, roleId);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/reset-password")
    @Operation(summary = "重置用户密码", description = "管理员重置指定用户的密码为默认密码")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> resetUserPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        // 调用服务层方法
        return userService.resetUserPassword(resetPasswordDTO.getUsername());
    }
}