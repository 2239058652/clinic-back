package com.dental.clinic.module.file.controller;

import com.dental.clinic.common.Result;
import com.dental.clinic.module.file.service.OssService;
import com.dental.clinic.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件接口", description = "文件上传相关的接口")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private OssService ossService;

    @Autowired
    private UserService userService; // 确保注入了 UserService

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像图片到OSS，并更新用户信息")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        logger.info("收到头像上传请求，文件名: {}", file.getOriginalFilename());

        // 1. 从 SecurityContext 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }

        String username = authentication.getName();
        // 调用 UserService 中的方法获取用户信息
        com.dental.clinic.module.user.entity.User currentUser = userService.getUserByUsername(username); // 确保此方法存在
        if (currentUser == null) {
            return Result.error("获取当前用户信息失败");
        }
        Long userId = currentUser.getId();

        try {
            // 2. 调用 OssService 上传文件
            String avatarUrl = ossService.uploadAvatar(file);

            // 3. 调用 UserService 更新用户头像信息
            Result<String> updateResult = userService.updateAvatar(userId, avatarUrl);

            if (updateResult.getCode() == 200) {
                return Result.success("头像上传成功", avatarUrl);
            } else {
                // 如果数据库更新失败，可以考虑删除刚上传的OSS文件（可选）
                return updateResult; // 直接返回更新失败的结果
            }

        } catch (Exception e) {
            logger.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像 到本地存储（开发环境使用）
     */
    @PostMapping("/upload/avatar/local")
    @Operation(summary = "上传头像到本地", description = "上传用户头像到本地存储（开发环境使用）")
    public Result<String> uploadAvatarToLocal(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 复用现有的用户验证逻辑
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }

        String username = authentication.getName();
        com.dental.clinic.module.user.entity.User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error("获取当前用户信息失败");
        }
        Long userId = currentUser.getId();

        try {
            // 使用本地存储方法
            String avatarUrl = "/api" + ossService.uploadAvatarToLocal(file);

            // 同样更新用户头像信息
            Result<String> updateResult = userService.updateAvatar(userId, avatarUrl);

            if (updateResult.getCode() == 200) {
                return Result.success("头像上传成功", avatarUrl);
            } else {
                return updateResult;
            }

        } catch (Exception e) {
            logger.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取本地头像
     */
    @GetMapping("/local/avatar/{filename}")
    @Operation(summary = "获取本地头像", description = "通过文件名获取本地存储的头像")
    public ResponseEntity<Resource> getLocalAvatar(@PathVariable String filename) {
        try {
            // 构建文件路径
            String filePath = "C:\\Users\\22390\\Desktop\\lgx\\" + filename;
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 获取文件MIME类型
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            logger.error("获取本地头像失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}