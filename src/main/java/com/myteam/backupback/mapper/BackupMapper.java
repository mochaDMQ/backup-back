package com.myteam.backupback.mapper;

import com.myteam.backupback.entity.Backup;

import java.util.List;

public interface BackupMapper {
    int insert(Backup backup);

    int deleteById(Integer id);

//    int updateById(Backup backup);

    Backup selectById(Integer id);

    List<Backup> selectAll(Backup backup);

}