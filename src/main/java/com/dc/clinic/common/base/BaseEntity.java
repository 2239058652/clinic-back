package com.dc.clinic.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO) // 声明主键，且是数据库自增
    private Integer id;

    @TableField(fill = FieldFill.INSERT) // 插入时自动填充时间
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充时间
    private LocalDateTime updatedAt;

    @TableLogic // 开启 MyBatis-Plus 的逻辑删除功能
    @TableField(fill = FieldFill.INSERT) // 默认为空，删除时会自动填入当前时间
    private LocalDateTime deletedAt;
}