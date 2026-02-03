package com.dc.clinic.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc.clinic.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@TableName("permissions")
public class Permission extends BaseEntity {
    private String code;      // 权限编码，如 user:list
    private String name;      // 权限名称
    private String type;      // MENU, BUTTON, API
    private Integer parentId; // 父级ID
    private String path;      // 路由地址
    private String component; // 组件路径
    private String icon;
    private Integer sort;
    private String status;
}