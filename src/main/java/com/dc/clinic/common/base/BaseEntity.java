package com.dc.clinic.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "createdAt", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updatedAt", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 修改逻辑删除配置
    @TableLogic(value = "NULL", delval = "NOW()")  // 未删除为 NULL，删除时设置为当前时间
    @TableField("deletedAt")
    private LocalDateTime deletedAt;
}