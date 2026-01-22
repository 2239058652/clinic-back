package com.dental.clinic.module.user.controller;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.user.entity.User;
import com.dental.clinic.module.captcha.dto.LoginRequest;
import com.dental.clinic.module.captcha.dto.LoginSuccessDTO;
import com.dental.clinic.module.file.dto.UpdateProfileDTO;
import com.dental.clinic.module.rolepermission.dto.UserInfoWithRolesDTO;
import com.dental.clinic.module.user.dto.RegisterUserDTO;
import com.dental.clinic.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户接口", description = "用户相关的接口")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录 (包含验证码)
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，需要提供用户名、密码和验证码信息")
    public Result<LoginSuccessDTO> login(@RequestBody @Valid LoginRequest loginRequest) { // 接收 DTO 并启用校验
        logger.info("用户登录请求: {}", loginRequest);
        // 从 DTO 中提取参数
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String captcha = loginRequest.getCaptcha();// 用户输入的验证码
        String captchaKey = loginRequest.getCaptchaKey(); // 验证码的唯一标识

        // 调用服务层进行登录验证
        return userService.login(username, password, captcha, captchaKey);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public Result<User> register(@RequestBody RegisterUserDTO registerUserDTO) {
        User user = new User();
        user.setUsername(registerUserDTO.getUsername());
        user.setPassword(registerUserDTO.getPassword()); // 后续在 Service 层加密
        user.setRealName(registerUserDTO.getRealName());
        user.setPhone(registerUserDTO.getPhone());
        user.setEmail(registerUserDTO.getEmail());
        user.setAvatar(registerUserDTO.getAvatar());
        // 设置默认角色和状态
        // user.setRole(1); // 默认角色为医生 // 注册时不再设置固定角色
        user.setStatus(1); // 默认状态为启用

        return userService.register(user); // 调用 Service，传入构建好的 User 对象
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public Result<UserInfoWithRolesDTO> getUserInfo(@PathVariable Long userId) { // 【修改】返回 DTO 包含角色信息
        return userService.getUserInfoWithRoles(userId);
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的基本信息")
    public Result<String> updateProfile(@RequestBody @Valid UpdateProfileDTO updateProfileDTO,
            HttpServletRequest request) { // @Valid 启用校验
        logger.info("收到更新个人资料请求: {}", updateProfileDTO);

        // 1. 从 SecurityContext 获取当前登录用户ID (与上传头像类似)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }

        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error("获取当前用户信息失败");
        }
        Long userId = currentUser.getId();

        // 2. 调用 Service 更新资料
        return userService.updateProfile(userId, updateProfileDTO);
    }

    @PostMapping("/request-role")
    @Operation(summary = "申请角色", description = "用户申请特定角色")
    public Result<String> requestRole(@RequestParam String requestedRoleCode) {
        // 1. 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error("获取当前用户信息失败");
        }
        Long userId = currentUser.getId();

        // 2. 检查请求的角色是否有效
        // 这里需要调用一个服务方法来验证角色是否存在
        if (!userService.isValidRoleCode(requestedRoleCode)) {
            return Result.error("无效的角色代码");
        }

        // 3. 记录申请（例如，存入一个申请表）
        // userService.recordRoleRequest(userId, requestedRoleCode);

        return Result.success("角色申请已提交，请等待管理员审批");
    }
}