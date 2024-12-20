package com.myteam.backupback.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * (File)实体类
 *
 * @author makejava
 * @since 2024-12-14 22:18:21
 */
@Getter
@Setter
public class Files implements Serializable {
    private static final long serialVersionUID = 664410640704262392L;

    private Integer id;

    private String fileName;

    private Integer backupId;

    private String checksum;

    private Long size;

}

