package com.dc.clinic.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dc.clinic.modules.system.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {
    // 如果以后要写复杂的日志统计查询，可以在这里定义

    void saveLog(OperationLog log);
}