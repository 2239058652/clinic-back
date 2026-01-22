package com.dental.clinic.module.rolepermission.controller;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.rolepermission.dto.CreateRoleDTO;
import com.dental.clinic.module.rolepermission.dto.CreatePermissionDTO;
import com.dental.clinic.module.rolepermission.dto.UpdateRoleDTO;
import com.dental.clinic.module.rolepermission.dto.UpdatePermissionDTO;
import com.dental.clinic.module.rolepermission.entity.Role;
import com.dental.clinic.module.rolepermission.entity.Permission;
import com.dental.clinic.module.rolepermission.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/roles-permissions") // 管理接口前缀
@Tag(name = "角色权限管理接口", description = "管理员用于管理角色、权限以及它们之间关联关系的接口")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    // --- 角色管理 ---

    /**
     * 创建角色
     */
    @PostMapping("/roles")
    @Operation(summary = "创建角色", description = "创建一个新的角色")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<Role> createRole(@RequestBody @Valid CreateRoleDTO createRoleDTO) {
        return rolePermissionService.createRole(createRoleDTO);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/roles/{roleId}")
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详细信息")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<Role> getRole(@PathVariable Long roleId) {
        return rolePermissionService.getRoleById(roleId);
    }

    /**
     * 获取所有角色列表
     */
    @GetMapping("/roles")
    @Operation(summary = "获取角色列表", description = "获取所有角色的列表")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<List<Role>> listRoles() {
        return rolePermissionService.getAllRoles();
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/roles/{roleId}")
    @Operation(summary = "更新角色信息", description = "根据角色ID更新角色信息")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> updateRole(@PathVariable Long roleId, @RequestBody @Valid UpdateRoleDTO updateRoleDTO) {
        return rolePermissionService.updateRole(roleId, updateRoleDTO);
    }

    /**
     * 删除角色（逻辑删除或物理删除，取决于实现）
     */
    @DeleteMapping("/roles/{roleId}")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> deleteRole(@PathVariable Long roleId) {
        return rolePermissionService.deleteRole(roleId);
    }

    // --- 权限管理 ---

    /**
     * 创建权限
     */
    @PostMapping("/permissions")
    @Operation(summary = "创建权限", description = "创建一个新的权限")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<Permission> createPermission(@RequestBody @Valid CreatePermissionDTO createPermissionDTO) {
        return rolePermissionService.createPermission(createPermissionDTO);
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/permissions/{permissionId}")
    @Operation(summary = "获取权限详情", description = "根据权限ID获取权限详细信息")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<Permission> getPermission(@PathVariable Long permissionId) {
        return rolePermissionService.getPermissionById(permissionId);
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping("/permissions")
    @Operation(summary = "获取权限列表", description = "获取所有权限的列表")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<List<Permission>> listPermissions() {
        return rolePermissionService.getAllPermissions();
    }

    /**
     * 更新权限信息
     */
    @PutMapping("/permissions/{permissionId}")
    @Operation(summary = "更新权限信息", description = "根据权限ID更新权限信息")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> updatePermission(@PathVariable Long permissionId, @RequestBody @Valid UpdatePermissionDTO updatePermissionDTO) {
        return rolePermissionService.updatePermission(permissionId, updatePermissionDTO);
    }

    /**
     * 删除权限（逻辑删除或物理删除，取决于实现）
     */
    @DeleteMapping("/permissions/{permissionId}")
    @Operation(summary = "删除权限", description = "根据权限ID删除权限")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> deletePermission(@PathVariable Long permissionId) {
        return rolePermissionService.deletePermission(permissionId);
    }

    // --- 角色权限关联管理 ---

    /**
     * 为角色分配权限
     */
    @PostMapping("/roles/{roleId}/permissions/{permissionId}")
    @Operation(summary = "为角色分配权限", description = "将指定权限分配给指定角色")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> assignPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return rolePermissionService.assignPermissionToRole(roleId, permissionId);
    }

    /**
     * 从角色移除权限
     */
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    @Operation(summary = "从角色移除权限", description = "从指定角色移除指定权限")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return rolePermissionService.removePermissionFromRole(roleId, permissionId);
    }

    /**
     * 获取角色拥有的所有权限
     */
    @GetMapping("/roles/{roleId}/permissions")
    @Operation(summary = "获取角色权限列表", description = "获取指定角色拥有的所有权限")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<List<Permission>> getPermissionsByRole(@PathVariable Long roleId) {
        return rolePermissionService.getPermissionsByRoleId(roleId);
    }

    // --- 用户角色关联管理 ---

    /**
     * 为用户分配角色
     */
    @PostMapping("/users/{userId}/roles/{roleId}")
    @Operation(summary = "为用户分配角色", description = "将指定角色分配给指定用户")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        return rolePermissionService.assignRoleToUser(userId, roleId);
    }

    /**
     * 从用户移除角色
     */
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    @Operation(summary = "从用户移除角色", description = "从指定用户移除指定角色")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<String> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        return rolePermissionService.removeRoleFromUser(userId, roleId);
    }

    /**
     * 获取用户拥有的所有角色
     */
    @GetMapping("/users/{userId}/roles")
    @Operation(summary = "获取用户角色列表", description = "获取指定用户拥有的所有角色")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    public Result<List<Role>> getRolesByUser(@PathVariable Long userId) {
        return rolePermissionService.getRolesByUserId(userId);
    }
}