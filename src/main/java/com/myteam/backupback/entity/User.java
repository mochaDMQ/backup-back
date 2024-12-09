package com.myteam.backupback.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AuthUser implements Serializable {
    private static final long serialVersionUID = -53077776455594055L;

    private Integer id;

    private String account;

    private String password;

    private String name;

    private String role;

}

