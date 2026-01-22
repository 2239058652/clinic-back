package com.dental.clinic.module.rolepermission.mapper;

import com.dental.clinic.module.rolepermission.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    Role selectById(@Param("id") Long id);

    Role selectByCode(@Param("roleCode") String roleCode);

    Role selectByName(@Param("roleName") String roleName);

    List<Role> selectAllNotDeleted();

    int insert(Role role);

    int updateById(Role role);

    int deleteById(@Param("id") Long id);
}