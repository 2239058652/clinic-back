package com.dc.clinic.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc.clinic.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("roles")
public class Role extends BaseEntity {
    private String code;        // 角色编码，如 ADMIN
    private String name;        // 角色名称，如 管理员
    private String description;
    private Integer sort;       // 排序
    private String status;      // ACTIVE, DISABLED
}