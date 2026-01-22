package com.dental.clinic.module.rolepermission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePermissionDTO {

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100个字符")
    private String permissionName;

    @NotBlank(message = "权限编码不能为空")
    @Pattern(regexp = "^[a-z:][a-z0-9_:]*$", message = "权限编码格式不正确，例如：user:read, patient:create")
    @Size(max = 50, message = "权限编码长度不能超过50个字符")
    private String permissionCode;

    private String description;
    private String resourceType; // 可选
    private String action;       // 可选
}