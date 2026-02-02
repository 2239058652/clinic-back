package com.dc.clinic.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.clinic.modules.system.entity.OperationLog;
import com.dc.clinic.modules.system.mapper.OperationLogMapper;
import com.dc.clinic.modules.system.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    // ServiceImpl 是 MyBatis-Plus 提供的模版，帮你把增删改查逻辑都写好了

    /**
     * 日志保存：独立事务
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(OperationLog log) {
        this.save(log);
    }
}