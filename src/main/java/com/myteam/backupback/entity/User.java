package com.myteam.backupback.entity;

import java.io.Serializable;

public class User extends AuthUser implements Serializable {
    private static final long serialVersionUID = -53077776455594055L;
/**
     * ID
     */
    private Integer id;
/**
     * 账号
     */
    private String account;
/**
     * 密码
     */
    private String password;
/**
     * 用户名
     */
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

