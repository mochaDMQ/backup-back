package com.myteam.backupback.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)

public class Admin extends AuthUser implements Serializable {
    private static final long serialVersionUID = -73548816720502233L;

    private Integer id;

    private String account;

    private String password;

    private String name;

    private String role;



}

