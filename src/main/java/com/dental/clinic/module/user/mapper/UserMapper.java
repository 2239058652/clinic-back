package com.dental.clinic.module.user.mapper;

import com.dental.clinic.module.user.dto.UserListDTO;
import com.dental.clinic.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    // 新增：查询所有用户 (用于分页和简单查询)
    List<User> selectAll(@Param("username") String username,
                         @Param("phone") String phone,
                         @Param("roleCode") String roleCode, // 添加角色筛选参数
                         @Param("status") Integer status);

    // 根据用户名查询用户 (只查未删除的)
    User selectByUsername(@Param("username") String username);

    int insert(User user);

    int updateById(User user); // 更新用户

    int deleteById(@Param("id") Long id);

    // 新增：根据ID查询未删除的用户
    User selectByIdAndNotDeleted(@Param("id") Long id);

    // 新增：根据用户名查询未删除的用户 和 启用状态的
    User selectByUsernameAndNotDeleted(@Param("username") String username);

    // 新增：查询所有未删除的用户
    List<User> selectAllNotDeleted();

    List<User> selectByAll();

    // 新增：查询用户的所有角色和权限
    List<String> selectUserRoles(@Param("userId") Long userId);
    List<String> selectUserPermissions(@Param("userId") Long userId);

    // 查询包含角色信息的用户列表（用于分页）
    List<UserListDTO> selectAllWithRoles(@Param("username") String username,
                                         @Param("phone") String phone,
                                         @Param("roleCode") String roleCode,
                                         @Param("status") Integer status);
}