package com.dental.clinic.module.rolepermission.service.impl;

import com.dental.clinic.module.rolepermission.entity.Role;
import com.dental.clinic.module.rolepermission.entity.Permission;
import com.dental.clinic.module.rolepermission.dto.CreatePermissionDTO;
import com.dental.clinic.module.rolepermission.dto.CreateRoleDTO;
import com.dental.clinic.module.rolepermission.dto.UpdatePermissionDTO;
import com.dental.clinic.module.rolepermission.dto.UpdateRoleDTO;
import com.dental.clinic.module.rolepermission.mapper.RoleMapper; // 需要创建
import com.dental.clinic.module.rolepermission.mapper.PermissionMapper; // 需要创建
import com.dental.clinic.module.rolepermission.mapper.UserRoleMapper; // 需要创建
import com.dental.clinic.module.rolepermission.mapper.RolePermissionMapper; // 需要创建
import com.dental.clinic.module.rolepermission.service.RolePermissionService;
import com.dental.clinic.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    // --- 角色管理 ---

    @Override
    @Transactional // 保证数据一致性
    public Result<Role> createRole(CreateRoleDTO createRoleDTO) {
        // 1. 检查角色编码是否已存在
        if (roleMapper.selectByCode(createRoleDTO.getRoleCode()) != null) {
            return Result.error("角色编码已存在");
        }
        // 2. 检查角色名称是否已存在
        if (roleMapper.selectByName(createRoleDTO.getRoleName()) != null) {
            return Result.error("角色名称已存在");
        }

        // 3. 创建角色对象
        Role role = new Role();
        role.setRoleName(createRoleDTO.getRoleName());
        role.setRoleCode(createRoleDTO.getRoleCode());
        role.setDescription(createRoleDTO.getDescription());
        role.setStatus(1); // 默认启用
        role.setDeleted(0); // 默认未删除
        role.setCreatedTime(LocalDateTime.now());
        role.setUpdatedTime(LocalDateTime.now());

        // 4. 保存到数据库
        roleMapper.insert(role);

        return Result.success("角色创建成功", role);
    }

    @Override
    public Result<Role> getRoleById(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @Override
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleMapper.selectAllNotDeleted();
        return Result.success(roles);
    }

    @Override
    @Transactional
    public Result<String> updateRole(Long roleId, UpdateRoleDTO updateRoleDTO) {
        Role existingRole = roleMapper.selectById(roleId);
        if (existingRole == null) {
            return Result.error("角色不存在");
        }

        // 检查名称是否与其他角色冲突（排除自己）
        Role roleWithName = roleMapper.selectByName(updateRoleDTO.getRoleName());
        if (roleWithName != null && !roleWithName.getId().equals(roleId)) {
             return Result.error("角色名称已存在");
        }

        existingRole.setRoleName(updateRoleDTO.getRoleName());
        existingRole.setDescription(updateRoleDTO.getDescription());
        existingRole.setUpdatedTime(LocalDateTime.now());

        int rows = roleMapper.updateById(existingRole);
        if (rows > 0) {
            return Result.success("角色更新成功");
        } else {
            return Result.error("角色更新失败");
        }
    }

    @Override
    @Transactional
    public Result<String> deleteRole(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        // 逻辑删除：更新 deleted 字段
        role.setDeleted(1);
        role.setUpdatedTime(LocalDateTime.now());
        int rows = roleMapper.updateById(role);

        if (rows > 0) {
            // 删除该角色关联的权限（可选，取决于业务逻辑）
            // rolePermissionMapper.deleteByRoleId(roleId);
            // 删除该角色分配给的用户（可选，取决于业务逻辑）
            // userRoleMapper.deleteByRoleId(roleId);
            return Result.success("角色删除成功 (逻辑删除)");
        } else {
            return Result.error("角色删除失败");
        }
    }

    // --- 权限管理 ---

    @Override
    @Transactional
    public Result<Permission> createPermission(CreatePermissionDTO createPermissionDTO) {
        // 1. 检查权限编码是否已存在
        Permission existingPermission = permissionMapper.selectByCode(createPermissionDTO.getPermissionCode());
        if (existingPermission != null) {
            if (existingPermission.getDeleted() == 0) {
                return Result.error("权限编码已存在");
            } else {
                return Result.error("权限编码已存在（曾被删除），请联系管理员恢复或使用其他编码");
            }
        }

        // 2. 创建权限对象
        Permission permission = new Permission();
        permission.setPermissionName(createPermissionDTO.getPermissionName());
        permission.setPermissionCode(createPermissionDTO.getPermissionCode());
        permission.setDescription(createPermissionDTO.getDescription());
        permission.setResourceType(createPermissionDTO.getResourceType());
        permission.setAction(createPermissionDTO.getAction());
        permission.setStatus(1); // 默认启用
        permission.setDeleted(0); // 默认未删除
        permission.setCreatedTime(LocalDateTime.now());
        permission.setUpdatedTime(LocalDateTime.now());

        // 3. 保存到数据库
        permissionMapper.insert(permission);

        return Result.success("权限创建成功", permission);
    }

    @Override
    public Result<Permission> getPermissionById(Long permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    @Override
    public Result<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionMapper.selectAllNotDeleted();
        return Result.success(permissions);
    }

    @Override
    @Transactional
    public Result<String> updatePermission(Long permissionId, UpdatePermissionDTO updatePermissionDTO) {
        Permission existingPermission = permissionMapper.selectById(permissionId);
        if (existingPermission == null) {
            return Result.error("权限不存在");
        }

        existingPermission.setPermissionName(updatePermissionDTO.getPermissionName());
        existingPermission.setDescription(updatePermissionDTO.getDescription());
        existingPermission.setResourceType(updatePermissionDTO.getResourceType());
        existingPermission.setAction(updatePermissionDTO.getAction());
        existingPermission.setUpdatedTime(LocalDateTime.now());

        int rows = permissionMapper.updateById(existingPermission);
        if (rows > 0) {
            return Result.success("权限更新成功");
        } else {
            return Result.error("权限更新失败");
        }
    }

    @Override
    @Transactional
    public Result<String> deletePermission(Long permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        // 逻辑删除：更新 deleted 字段
        permission.setDeleted(1);
        permission.setUpdatedTime(LocalDateTime.now());
        int rows = permissionMapper.updateById(permission);

        if (rows > 0) {
            // 删除该权限关联的角色（可选，取决于业务逻辑）
            // rolePermissionMapper.deleteByPermissionId(permissionId);
            return Result.success("权限删除成功 (逻辑删除)");
        } else {
            return Result.error("权限删除失败");
        }
    }

    // --- 角色权限关联管理 ---

    @Override
    @Transactional
    public Result<String> assignPermissionToRole(Long roleId, Long permissionId) {
        // 1. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        // 2. 检查权限是否存在
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            return Result.error("权限不存在");
        }

        // 3. 检查关联是否已存在
        if (rolePermissionMapper.selectByRoleIdAndPermissionId(roleId, permissionId) != null) {
            return Result.error("角色已拥有该权限");
        }

        // 4. 创建关联
        rolePermissionMapper.insert(roleId, permissionId);

        return Result.success("权限分配成功");
    }

    @Override
    @Transactional
    public Result<String> removePermissionFromRole(Long roleId, Long permissionId) {
        // 1. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        // 2. 检查权限是否存在
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            return Result.error("权限不存在");
        }

        // 3. 删除关联
        int rows = rolePermissionMapper.deleteByRoleIdAndPermissionId(roleId, permissionId);
        if (rows > 0) {
            return Result.success("权限移除成功");
        } else {
            return Result.error("权限移除失败或关联不存在");
        }
    }

    @Override
    public Result<List<Permission>> getPermissionsByRoleId(Long roleId) {
        // 1. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }
        // 2. 查询角色拥有的权限
        List<Permission> permissions = rolePermissionMapper.selectPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    // --- 用户角色关联管理 ---

    @Override
    @Transactional
    public Result<String> assignRoleToUser(Long userId, Long roleId) {
        // 1. 检查用户是否存在 (可以调用 UserMapper 或 UserService)
        // if (userMapper.selectById(userId) == null) { return Result.error("用户不存在"); }
        // 2. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }

        // 3. 检查关联是否已存在
        if (userRoleMapper.selectByUserIdAndRoleId(userId, roleId) != null) {
            return Result.error("用户已拥有该角色");
        }

        // 4. 创建关联
        userRoleMapper.insert(userId, roleId);

        return Result.success("角色分配成功");
    }

    @Override
    @Transactional
    public Result<String> removeRoleFromUser(Long userId, Long roleId) {
        // 1. 检查用户是否存在 (可以调用 UserMapper 或 UserService)
        // if (userMapper.selectById(userId) == null) { return Result.error("用户不存在"); }
        // 2. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.error("角色不存在");
        }

        // 3. 删除关联
        int rows = userRoleMapper.deleteByUserIdAndRoleId(userId, roleId);
        if (rows > 0) {
            return Result.success("角色移除成功");
        } else {
            return Result.error("角色移除失败或关联不存在");
        }
    }

    @Override
    public Result<List<Role>> getRolesByUserId(Long userId) {
        // 1. 检查用户是否存在 (可以调用 UserMapper 或 UserService)
        // if (userMapper.selectById(userId) == null) { return Result.error("用户不存在"); }
        // 2. 查询用户拥有的角色
        List<Role> roles = userRoleMapper.selectRolesByUserId(userId);
        return Result.success(roles);
    }
}