package com.dental.clinic.module.rolepermission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePermissionDTO {

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100个字符")
    private String permissionName;

    private String description;
    private String resourceType; // 可选
    private String action;       // 可选
    // 注意：通常不建议修改 permissionCode
}