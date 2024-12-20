package com.myteam.backupback.mapper;
import com.myteam.backupback.entity.Files;

import java.util.List;


public interface FilesMapper {
    int insert(Files file);

    int batchInsert(List<Files> files);

    List<Files> selectByBackupId(Integer id);

}
