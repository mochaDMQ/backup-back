package com.myteam.backupback.entity;

import lombok.Data;

// 父类
@Data
public class AuthUser {
    private Integer id;
    private String account;
    private String name;
    private String password;
    private String role;
    private String newPassword;
    private String token;

}
