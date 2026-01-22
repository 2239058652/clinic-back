package com.dental.clinic.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询请求参数")
public class UserQueryRequest {

    @Schema(description = "页码，从1开始")
    private Integer page = 1;

    @Schema(description = "每页数量")
    private Integer size = 10;

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色编码（用于筛选，例如：DOCTOR, NURSE, ADMIN）")
    private String roleCode; // 改为 roleCode，更符合新结构

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;
}