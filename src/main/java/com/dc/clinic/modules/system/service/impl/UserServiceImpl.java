package com.dc.clinic.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.clinic.modules.system.entity.User;
import com.dc.clinic.modules.system.mapper.UserMapper;
import com.dc.clinic.modules.system.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 自动实现了 IService 接口中的所有方法
}