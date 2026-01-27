package com.dc.clinic.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.clinic.modules.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询所有权限编码
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Integer userId);
}