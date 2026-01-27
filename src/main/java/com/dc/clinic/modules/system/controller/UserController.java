package com.dc.clinic.modules.system.controller;

import com.dc.clinic.common.response.Result;
import com.dc.clinic.modules.system.entity.User;
import com.dc.clinic.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据ID查询用户：http://localhost:9095/system/user/1
    @PreAuthorize("hasAuthority('user:create')")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("未找到该用户");
        }
    }
}