package com.dc.clinic.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc.clinic.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users") // 对应数据库 users 表
public class User extends BaseEntity {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String status; // ACTIVE, DISABLED, LOCKED
}