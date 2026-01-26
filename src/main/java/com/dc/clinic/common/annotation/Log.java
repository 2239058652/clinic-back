package com.dc.clinic.common.annotation;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /** 模块名称 (如：用户管理) */
    String title() default "";

    /** 业务类型 (如：UPDATE, DELETE) */
    String businessType() default "OTHER";
}