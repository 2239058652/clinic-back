package com.dc.clinic.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.clinic.modules.system.entity.Permission;
import com.dc.clinic.modules.system.mapper.PermissionMapper;
import com.dc.clinic.modules.system.service.PermissionService;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
}
