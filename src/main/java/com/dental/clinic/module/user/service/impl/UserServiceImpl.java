package com.dental.clinic.module.user.service.impl;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.captcha.dto.LoginSuccessDTO;
import com.dental.clinic.module.file.dto.UpdateProfileDTO;
import com.dental.clinic.module.rolepermission.dto.UserInfoWithRolesDTO;
import com.dental.clinic.module.rolepermission.entity.Role;
import com.dental.clinic.module.rolepermission.mapper.RoleMapper;
import com.dental.clinic.module.rolepermission.mapper.UserRoleMapper;
import com.dental.clinic.module.user.dto.UserListDTO;
import com.dental.clinic.module.user.dto.UserQueryRequest;
import com.dental.clinic.module.user.entity.User;
import com.dental.clinic.module.user.mapper.UserMapper;
import com.dental.clinic.module.user.service.UserService;
import com.dental.clinic.module.captcha.service.CaptchaService;
import com.dental.clinic.util.JwtUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CaptchaService captchaService; // 注入 CaptchaService

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    // 【新增】配置默认密码
    @Value("${app.default-reset-password:Adc123}") // 从配置文件读取默认密码，如果未配置则使用 "123456"
    private String defaultResetPassword;

    // 登录
    @Override
    public Result<LoginSuccessDTO> login(String username, String password, String captchaCode, String captchaKey) {
        // 1. 验证验证码
        if(captchaCode == null || captchaKey == null || !captchaService.verifyCaptcha(captchaKey, captchaCode)) {
            return Result.error("验证码错误或已过期");
        }

        // 2. 验证用户名和密码 （使用spring security）
        try {
            // 注意：这里的 password 是用户输入的明文密码
            // Spring Security 的 DaoAuthenticationProvider 会自动使用 PasswordEncoder 来比对
            // 2. 使用 Spring Security 的 AuthenticationManager 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 3. 认证成功，生成 JWT Token
            String token = jwtUtil.generateToken(username);

            // 4. 获取完整的用户信息
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                // 理论上不应该发生，因为认证已经成功
                return Result.error("用户信息获取失败");
            }
            // 检查用户是否被禁用
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.error(403,"用户已被禁用");
            }
            // 检查用户是否被逻辑删除
            if (user.getDeleted() != null && user.getDeleted() == 1) {
                return Result.error(404,"账户不存在或已被删除");
            }

            // 5. 【新增】获取用户的角色列表
            List<String> roles = userMapper.selectUserRoles(user.getId());

            // 6. 构造返回对象
            LoginSuccessDTO loginSuccessDTO = new LoginSuccessDTO(token, user, roles); // 传入 roles

            // 6. 可选：将用户信息存入 SecurityContext (虽然过滤器会自动处理)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 7. 返回 Token 和用户信息
            return Result.success("登录成功", loginSuccessDTO);

        } catch (UsernameNotFoundException e) {
            // 捕获特定异常：用户名不存在
            return Result.error("用户不存在");
        } catch (BadCredentialsException e) {
            // 捕获特定异常：用户名存在但密码错误
            return Result.error("密码错误");
        } catch (Exception e) {
            // 捕获其他可能的认证异常（虽然上面两个基本覆盖了主要情况）
            // 也可以记录日志以便排查问题
            // log.error("登录过程发生未知异常", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    // 注册
    @Override
    public Result<User> register(User user) {
        // 1. 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 2. 设置默认值和时间戳
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        // 3. 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. 保存用户
        userMapper.insert(user);

        // 5. 注册成功，返回用户信息 新注册的用户没有角色，需要管理员手动分配
        return Result.success("注册成功", user);
    }

    // 获取用户信息
    @Override
    public Result<User> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    // 【新增】获取用户信息（包含角色）
    @Override
    public Result<UserInfoWithRolesDTO> getUserInfoWithRoles(Long userId) {
        // 1. 查询用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 查询用户的角色列表
        List<String> roles = userMapper.selectUserRoles(userId);

        // 3. 构造返回的 DTO
        UserInfoWithRolesDTO userInfoWithRoles = new UserInfoWithRolesDTO(user, roles);

        return Result.success("获取用户信息成功", userInfoWithRoles);
    }

    // 实现更新个人资料方法
    @Override
    public Result<String> updateProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
        // 1. 查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 2. 更新用户信息
        existingUser.setRealName(updateProfileDTO.getRealName());
        existingUser.setPhone(updateProfileDTO.getPhone());
        existingUser.setEmail(updateProfileDTO.getEmail());
        existingUser.setUpdatedTime(LocalDateTime.now()); // 更新时间戳

        // 3. 保存到数据库
        int rowsUpdated = userMapper.updateById(existingUser);
        if (rowsUpdated > 0) {
            return Result.success("个人资料更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    // 实现更新头像方法
    @Override
    public Result<String> updateAvatar(Long userId, String avatarUrl) {
        // 1. 查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 2. 更新头像URL
        existingUser.setAvatar(avatarUrl);
        existingUser.setUpdatedTime(LocalDateTime.now()); // 更新时间戳

        // 3. 保存到数据库
        int rowsUpdated = userMapper.updateById(existingUser);
        if (rowsUpdated > 0) {
            return Result.success("头像更新成功");
        } else {
            return Result.error("头像更新失败");
        }
    }

    // 根据用户名获取用户
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username); // 调用 Mapper 方法
    }

    // 创建用户
    @Override
    public Result<User> createUser(User user) {
        // 1. 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 2. 设置默认值和时间戳
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        // 3. 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. 保存用户
        userMapper.insert(user);

        // 5. 注册成功，返回用户信息
        return Result.success("用户创建成功", user);
    }

    // 获取用户列表
    @Override
    public Result<User> getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    // 获取用户列表
    @Override
    public Result<PageInfo<User>> listUsers(UserQueryRequest query) {
        // 1. 开启分页
        @Cleanup
        Page<Object> page = PageHelper.startPage(query.getPage(), query.getSize());

        // 2. 查询所有用户 (或根据条件查询)
        List<User> users = userMapper.selectAll(query.getUsername(),
                query.getPhone(),
                query.getRoleCode(), // 移除这个参数！
                query.getStatus()); // 你需要在 UserMapper 中添加 selectAll 方法

        // 3. 封装分页信息
        PageInfo<User> pageInfo = new PageInfo<>(users);

        // 4. 返回结果
        return Result.success("查询成功", pageInfo);
    }

    // 获取用户列表，返回包含角色信息的 DTO
    @Override
    @Transactional(readOnly = true) // 查询操作，设置为只读
    public Result<PageInfo<UserListDTO>> listUsersWithRoles(UserQueryRequest query) { // 修改返回类型
        // 1. 开启分页
        @Cleanup
        Page<Object> page = PageHelper.startPage(query.getPage(), query.getSize());

        // 2. 查询包含角色信息的用户列表
        List<UserListDTO> usersWithRoles = userMapper.selectAllWithRoles( // 修改返回类型
                query.getUsername(),
                query.getPhone(),
                query.getRoleCode(), // 使用新的 roleCode 参数
                query.getStatus()
        );

        // 3. 封装分页信息
        PageInfo<UserListDTO> pageInfo = new PageInfo<>(usersWithRoles); // 修改类型

        // 4. 返回结果
        return Result.success("查询成功", pageInfo); // 修改类型
    }

    // 更新用户信息
    @Override
    public Result<String> updateUser(User user) {
        // 1. 查询用户是否存在
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 2. 更新用户信息 (排除用户名、密码等)
        // 这里可以根据需要决定哪些字段可以更新
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        // existingUser.setRole(user.getRole());
        existingUser.setUpdatedTime(LocalDateTime.now()); // 更新时间戳
        existingUser.setStatus(user.getStatus()); // 更新状态

        // 3. 保存到数据库
        int rowsUpdated = userMapper.updateById(existingUser);
        if (rowsUpdated > 0) {
            return Result.success("用户信息更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    // 删除用户
    @Override
    public Result<String> deleteUser(Long userId) {
        // 1. 查询用户是否存在 (查询包括已删除的用户，或者只查未删除的用户，取决于你的逻辑)
        // 这里选择查询未删除的用户，如果查不到，说明用户不存在或已被删除
        User existingUser = userMapper.selectByIdAndNotDeleted(userId);
        if (existingUser == null) {
            return Result.error("用户不存在或已被删除");
        }

        // 2. 逻辑删除用户 (更新 deleted 字段)
        existingUser.setDeleted(1); // 标记为已删除
        existingUser.setUpdatedTime(LocalDateTime.now()); // 更新时间戳
        int rowsUpdated = userMapper.updateById(existingUser);

        if (rowsUpdated > 0) {
            return Result.success("用户删除成功 (逻辑删除)");
        } else {
            return Result.error("删除失败");
        }
    }

    // 实现禁用用户方法
    @Override
    public Result<String> changeUserStatus(Long userId, Integer status) {
        // 1. 查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 2. 更新状态
        existingUser.setStatus(status);
        existingUser.setUpdatedTime(LocalDateTime.now());

        // 3. 保存到数据库
        int rowsUpdated = userMapper.updateById(existingUser);
        if (rowsUpdated > 0) {
            String statusStr = status == 1 ? "启用" : "禁用";
            return Result.success("用户" + statusStr + "成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    // 可选：简单查询所有用户（用于 AdminController 中的 listUsers 示例）
    @Override
    public Result<List<User>> listAllUsers() {
        List<User> users = userMapper.selectByAll(); // 你需要在 UserMapper 中添加 selectByAll 方法
        return Result.success("查询成功", users);
    }

    @Override
    public Result<String> assignRoleToUser(Long userId, Long roleId) {
        // 1. 检查用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        // 2. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) { return Result.error("角色不存在"); }

        // 3. 检查关联是否已存在 检查 user_roles 表中是否已经存在该用户的该角色关联
        // 可以通过查询 user_roles 表实现，或者在插入时处理唯一键冲突
        boolean isUserRoleExists = userRoleMapper.isUserRoleExists(userId, roleId);
        if (isUserRoleExists) { return Result.success("角色已分配, 无需重复分配"); }

        // 4. 插入 user_roles 关联
        try {
            userRoleMapper.insert(userId, roleId);
            return Result.success("角色分配成功");
        } catch (Exception e) {
            return Result.error("角色分配失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> removeRoleFromUser(Long userId, Long roleId) {
        // 1. 检查用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        // 2. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) { return Result.error("角色不存在"); }

        // 3. 删除 user_roles 关联 // TODO
        try {
            // userRolesMapper.delete(userId, roleId); // 需要实现
            // executeDeleteUserRole(userId, roleId); // 需要你自己实现
            int rows = userRoleMapper.deleteByUserIdAndRoleId(userId, roleId);
            if (rows > 0) {
                return Result.success("角色移除成功");
            } else {
                return Result.error("角色移除失败或关联不存在");
            }
        } catch (Exception e) {
            return Result.error("角色移除失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isValidRoleCode(String roleCode) {
        // 调用 RoleMapper 查询
        // return roleMapper.selectByCode(roleCode) != null;
        // 需要创建 RoleMapper
        return true; // 临时返回 true，实际需要实现
    }

    // 重置用户密码
    @Override
    @Transactional
    public Result<String> resetUserPassword(String username) {
        // 1. 检查用户是否存在
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }

        // 2. 检查用户是否被删除或禁用
        if (existingUser.getDeleted() != null && existingUser.getDeleted() == 1) {
            return Result.error("用户已被删除，无法重置密码");
        }
        if (existingUser.getStatus() != null && existingUser.getStatus() == 0) {
            return Result.error("用户已被禁用，无法重置密码");
        }

        // 3. 检查是否是管理员自己重置自己的密码 (可选的安全检查)
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.getName().equals(username)) {
            return Result.error("不能重置自己的密码，请使用个人中心的修改密码功能");
        }

        // 4. 设置新的默认密码并加密
        String encodedNewPassword = passwordEncoder.encode(defaultResetPassword);

        // 5. 更新数据库
        existingUser.setPassword(encodedNewPassword);
        existingUser.setUpdatedTime(LocalDateTime.now());
        int rowsUpdated = userMapper.updateById(existingUser);

        if (rowsUpdated > 0) {
            // 6. 返回成功信息
            return Result.success("密码重置成功，新密码为: " + defaultResetPassword);
        } else {
            return Result.error("密码重置失败");
        }
    }

    // 【重要】为了实现 assignRoleToUser 和 removeRoleFromUser，你需要创建 UserRolesMapper
    // 或者使用 @Autowired JdbcTemplate 来直接执行 SQL
    // 例如：
    // @Autowired
    // private JdbcTemplate jdbcTemplate;

    // private void executeInsertUserRole(Long userId, Long roleId) {
    //     String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
    //     jdbcTemplate.update(sql, userId, roleId);
    // }
    // private void executeDeleteUserRole(Long userId, Long roleId) {
    //     String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
    //     jdbcTemplate.update(sql, userId, roleId);
    // }
}