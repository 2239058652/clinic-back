package com.dc.clinic.modules.patient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dc.clinic.modules.patient.entity.Patient;
import com.dc.clinic.modules.patient.mapper.PatientMapper;
import com.dc.clinic.modules.patient.service.PatientService;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

}
