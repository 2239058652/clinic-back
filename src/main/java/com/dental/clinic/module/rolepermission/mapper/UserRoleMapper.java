package com.dental.clinic.module.rolepermission.mapper;

import com.dental.clinic.module.rolepermission.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    // 查询用户拥有的角色
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    // 检查用户角色关联是否存在
    Role selectByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    // 为用户分配角色
    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    // 从用户移除角色
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    // (可选) 根据角色ID删除所有关联
    // int deleteByRoleId(@Param("roleId") Long roleId);

    // 检查是否存在该用户的该角色关联怎么做
    boolean isUserRoleExists(@Param("userId") Long userId, @Param("roleId") Long roleId);
}