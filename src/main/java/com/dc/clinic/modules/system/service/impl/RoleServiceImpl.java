package com.dc.clinic.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.clinic.modules.system.entity.Role;
import com.dc.clinic.modules.system.mapper.RoleMapper;
import com.dc.clinic.modules.system.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
