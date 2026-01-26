package com.dc.clinic.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.clinic.modules.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 自动获得增删改查能力
}