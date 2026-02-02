package com.dc.clinic.modules.patient.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dc.clinic.common.exception.ServiceException;
import com.dc.clinic.common.response.Result;
import com.dc.clinic.modules.patient.entity.Patient;
import com.dc.clinic.modules.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 新增患者
     */
    @PreAuthorize("hasAuthority('patient:create')")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Patient patient) {
        // 检查身份证号是否已存在（业务校验）
        long count = patientService.count(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getIdCard, patient.getIdCard()));

        if (count > 0) {
            // 触发你刚才写的全局异常处理
            throw new ServiceException("该身份证号对应的患者已存在");
        }

        return Result.success(patientService.save(patient));
    }
}
