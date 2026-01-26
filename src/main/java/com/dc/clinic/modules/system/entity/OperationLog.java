package com.dc.clinic.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dc.clinic.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("operation_logs") // 必须对应数据库表名
public class OperationLog extends BaseEntity {
    
    private String title;          // 模块标题
    private String businessType;   // 业务类型
    private String method;         // 方法名称
    private String requestMethod;  // HTTP请求方式
    private String operatorName;   // 操作人员
    private String operUrl;        // 请求URL
    private String operIp;         // 主机地址
    private String operParam;      // 请求参数
    private String jsonResult;     // 返回结果
    private Integer status;        // 操作状态 (0正常 1异常)
    private String errorMsg;       // 错误消息
}