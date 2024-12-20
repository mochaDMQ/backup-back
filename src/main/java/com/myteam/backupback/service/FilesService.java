package com.myteam.backupback.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import com.myteam.backupback.entity.Files;
import com.myteam.backupback.mapper.FilesMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FilesService {
    @Resource
    private FilesMapper filesMapper;

    private static final Log log = LogFactory.get();

    public List<Files> selectByBackupId(Integer id){
        return filesMapper.selectByBackupId(id);
    }

    public void insert(Files file){
        filesMapper.insert(file);
    }

    public void batchInsert(List<Files> files){
        filesMapper.batchInsert(files);
    }


}
