package com.myteam.backupback.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class Backup implements Serializable {
    private static final long serialVersionUID = 956945060342954810L;

    private Integer id;

    private String backupName;

    private Date createdAt;

    private Integer userId;
}

