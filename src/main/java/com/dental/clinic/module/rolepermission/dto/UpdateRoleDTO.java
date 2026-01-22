package com.dental.clinic.module.rolepermission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRoleDTO {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    private String description;
    // 注意：通常不建议修改 roleCode，因为它可能被硬编码在代码中或作为权限前缀
}