package com.dc.clinic.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("operation_logs")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    // 如果是 businessType
    @TableField("businessType")
    private String businessType;

    private String method;

    @TableField("requestMethod")
    private String requestMethod;

    @TableField("operatorName")
    private String operatorName;

    @TableField("operUrl")
    private String operUrl;

    @TableField("operIp")
    private String operIp;

    @TableField("operParam")
    private String operParam;

    @TableField("jsonResult")
    private String jsonResult;

    private Integer status;

    @TableField("errorMsg")
    private String errorMsg;

    @TableField("operTime")
    private Date operTime;
}