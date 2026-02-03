package com.dc.clinic.modules.patient.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc.clinic.common.exception.ServiceException;
import com.dc.clinic.common.response.Result;
import com.dc.clinic.modules.patient.entity.Patient;
import com.dc.clinic.modules.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "患者管理", description = "提供患者信息的增删改查接口")
@RestController
@RequestMapping("/patient")
public class PatientController {

    // 1. 使用 final 保证不可变
    private final PatientService patientService;

    // 2. 显式构造函数注入（Spring 4.3+ 以后，如果只有一个构造函数，@Autowired 可省略）
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 新增患者
     */
    @Operation(summary = "新增患者")
    @PreAuthorize("hasAuthority('patient:create')")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Patient patient) {
        // 检查身份证号是否已存在（只查未删除的记录）
        long count = patientService.count(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getIdCard, patient.getIdCard())
                .isNull(Patient::getDeletedAt));

        if (count > 0) {
            throw new ServiceException("该身份证号对应的患者已存在");
        }

        return Result.success(patientService.save(patient));
    }

    /**
     * 分页查询患者列表 (支持姓名模糊搜索)
     */
    @Operation(summary = "分页查询患者列表", description = "支持姓名模糊搜索")
    @PreAuthorize("hasAuthority('patient:list')")
    @GetMapping("/list")
    public Result<IPage<Patient>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "患者姓名（模糊查询，可选）", required = false, example = "")
            @RequestParam(required = false) String name) {

        Page<Patient> page = new Page<>(current, size);
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();

        // 如果传了姓名，就进行模糊查询
        wrapper.like(StringUtils.hasText(name), Patient::getName, name);
        // 按创建时间倒序
        wrapper.orderByDesc(Patient::getCreatedAt);

        return Result.success(patientService.page(page, wrapper));
    }
}
