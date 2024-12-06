package com.myteam.backupback.entity;

import java.io.Serializable;

public class Admin extends AuthUser implements Serializable {
    private static final long serialVersionUID = -73548816720502233L;

    private Integer id;

    private String account;

    private String password;

    private String name;

    private String role;


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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

