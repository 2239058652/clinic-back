package com.dental.clinic.module.rolepermission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoleDTO {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z_][A-Z0-9_]*$", message = "角色编码必须以字母或下划线开头，由大写字母、数字、下划线组成")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String roleCode;

    private String description;
}