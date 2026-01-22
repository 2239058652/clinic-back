package com.dental.clinic.module.rolepermission.mapper;

import com.dental.clinic.module.rolepermission.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    Permission selectById(@Param("id") Long id);

    Permission selectByCode(@Param("permissionCode") String permissionCode);

    List<Permission> selectAllNotDeleted();

    int insert(Permission permission);

    int updateById(Permission permission);

    int deleteById(@Param("id") Long id);
}