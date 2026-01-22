package com.dental.clinic.module.rolepermission.mapper;

import com.dental.clinic.module.rolepermission.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper {

    // 查询角色拥有的权限
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    // 检查角色权限关联是否存在
    Permission selectByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    // 为角色分配权限
    int insert(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    // 从角色移除权限
    int deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    // (可选) 根据角色ID删除所有关联
    // int deleteByRoleId(@Param("roleId") Long roleId);
    // (可选) 根据权限ID删除所有关联
    // int deleteByPermissionId(@Param("permissionId") Long permissionId);
}