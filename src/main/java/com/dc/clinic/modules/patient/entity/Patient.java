package com.dc.clinic.modules.patient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("patients")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String gender; // 枚举或字符串：MALE/FEMALE
    private Integer age;
    private String phone;
    private String idCard; // 身份证号
    private String address;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic // 逻辑删除
    private LocalDateTime deletedAt;
}