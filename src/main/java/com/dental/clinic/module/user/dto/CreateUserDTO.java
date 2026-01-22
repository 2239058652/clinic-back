package com.dental.clinic.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6到100个字符之间")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    // @NotNull(message = "角色不能为空") // 注释掉
    // private Integer role; // 1-医生, 2-护士, 3-管理员 // 注释掉

    @NotNull
    private Integer status; // 1-启用, 0-禁用

    // 其他字段根据需求添加
    // 例如：科室、职位、入职时间等
    // 注意：这里不包含 id, avatar, status, createdTime, updatedTime 等字段
    // id 由数据库自动生成，status 默认为 1 (启用)，avatar 可选，时间戳由 Service 层设置
}