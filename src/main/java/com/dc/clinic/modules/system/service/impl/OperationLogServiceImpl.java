package com.dc.clinic.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.clinic.modules.system.entity.OperationLog;
import com.dc.clinic.modules.system.mapper.OperationLogMapper;
import com.dc.clinic.modules.system.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    // ServiceImpl 是 MyBatis-Plus 提供的模版，帮你把增删改查逻辑都写好了
}