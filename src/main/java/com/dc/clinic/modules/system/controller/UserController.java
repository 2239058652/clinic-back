package com.dc.clinic.modules.system.controller;

import com.dc.clinic.common.response.Result;
import com.dc.clinic.modules.system.entity.User;
import com.dc.clinic.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据ID查询用户：http://localhost:9095/system/user/1
    @PreAuthorize("hasAuthority('user:create')")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }
}