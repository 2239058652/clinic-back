package com.dental.clinic.module.rolepermission.service;

import com.dental.clinic.module.rolepermission.entity.Role;
import com.dental.clinic.module.rolepermission.entity.Permission;
import com.dental.clinic.common.Result;
import com.dental.clinic.module.rolepermission.dto.CreatePermissionDTO;
import com.dental.clinic.module.rolepermission.dto.CreateRoleDTO;
import com.dental.clinic.module.rolepermission.dto.UpdatePermissionDTO;
import com.dental.clinic.module.rolepermission.dto.UpdateRoleDTO;

import java.util.List;

public interface RolePermissionService {

    // --- 角色管理 ---
    Result<Role> createRole(CreateRoleDTO createRoleDTO);
    Result<Role> getRoleById(Long roleId);
    Result<List<Role>> getAllRoles();
    Result<String> updateRole(Long roleId, UpdateRoleDTO updateRoleDTO);
    Result<String> deleteRole(Long roleId);

    // --- 权限管理 ---
    Result<Permission> createPermission(CreatePermissionDTO createPermissionDTO);
    Result<Permission> getPermissionById(Long permissionId);
    Result<List<Permission>> getAllPermissions();
    Result<String> updatePermission(Long permissionId, UpdatePermissionDTO updatePermissionDTO);
    Result<String> deletePermission(Long permissionId);

    // --- 角色权限关联管理 ---
    Result<String> assignPermissionToRole(Long roleId, Long permissionId);
    Result<String> removePermissionFromRole(Long roleId, Long permissionId);
    Result<List<Permission>> getPermissionsByRoleId(Long roleId);

    // --- 用户角色关联管理 ---
    Result<String> assignRoleToUser(Long userId, Long roleId);
    Result<String> removeRoleFromUser(Long userId, Long roleId);
    Result<List<Role>> getRolesByUserId(Long userId);
}