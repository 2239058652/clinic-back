package com.dental.clinic.controller;

import com.dental.clinic.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    @Operation(summary = "测试接口")
    public Result<String> hello() {
        return Result.success("Hello, Auto Swagger!");
    }
}