package com.dental.clinic.module.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "用户列表项 DTO")
public class UserListDTO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    // 注意：通常不会返回密码给前端！
    // private String password;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @Schema(description = "逻辑删除：0-未删除 1-已删除")
    private Integer deleted;

    @Schema(description = "用户拥有的角色编码列表")
    private List<String> roles; // 这是关键新增字段

    // 无参构造函数，MyBatis 需要它
    public UserListDTO() {
    }

    // 构造函数：从 User 和 roles 列表创建 DTO
    public UserListDTO(com.dental.clinic.module.user.entity.User user, List<String> roles) {
        this.id = user.getId();
        this.username = user.getUsername();
        // this.password = user.getPassword(); // 不设置密码
        this.realName = user.getRealName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.status = user.getStatus();
        this.createdTime = user.getCreatedTime();
        this.updatedTime = user.getUpdatedTime();
        this.deleted = user.getDeleted();
        this.roles = roles;
    }
}