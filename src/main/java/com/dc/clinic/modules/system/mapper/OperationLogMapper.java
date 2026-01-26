package com.dc.clinic.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dc.clinic.modules.system.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    // 继承了 BaseMapper，你就拥有了 insert, update, selectById 等所有基础功能
}