package com.myteam.backupback.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.myteam.backupback.common.Result;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.service.AdminService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 基础前端接口
 **/

@RestController
public class WebController {
    @Resource
    private AdminService adminService;

    @GetMapping("/")
    public Result hello(){return Result.success("访问成功");}

    // 登录界面
    @PostMapping("/login")
    public Result login(@RequestBody AuthUser authUser){
        if(ObjectUtil.isEmpty(authUser.getAccount())
                ||ObjectUtil.isEmpty(authUser.getPassword())){
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        // 使用管理员登录
        if(RoleEnum.ADMIN.name().equals(authUser.getRole())){
            authUser = adminService.login(authUser);
        }
        return Result.success(authUser);
    }

    @PostMapping("/register")
    public Result register(@RequestBody AuthUser authUser){
        if (StrUtil.isBlank(authUser.getAccount()) || StrUtil.isBlank(authUser.getPassword())
                || ObjectUtil.isEmpty(authUser.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(authUser.getRole())) {
            adminService.register(authUser);
        }
        return Result.success();
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody AuthUser authUser) {
        if (StrUtil.isBlank(authUser.getAccount()) || StrUtil.isBlank(authUser.getPassword())
                || ObjectUtil.isEmpty(authUser.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(authUser.getRole())) {
            adminService.updatePassword(authUser);
        }
        return Result.success();
    }
}
