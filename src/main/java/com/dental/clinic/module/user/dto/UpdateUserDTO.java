package com.dental.clinic.module.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    // @NotNull(message = "角色不能为空") // 暂时注释掉
    // private Integer role; // 1-医生, 2-护士, 3-管理员

    @NotNull(message = "状态不能为空")
    private Integer status; // 0-禁用, 1-启用
    // 注意：这里不包含 username, password 等通常不允许管理员直接修改的字段
    // 也不包含 id, avatar, createdTime, updatedTime
}