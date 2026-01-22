package com.dental.clinic.module.user.dto;

import com.dental.clinic.module.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "包含角色信息的用户 DTO")
public class UserWithRolesDTO {

    @Schema(description = "用户基本信息")
    private User user;

    @Schema(description = "用户拥有的角色编码列表")
    private List<String> roles;
}