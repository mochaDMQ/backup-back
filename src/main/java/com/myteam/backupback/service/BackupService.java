package com.myteam.backupback.service;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.Result;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.Backup;
import com.myteam.backupback.entity.AuthUser;
import com.myteam.backupback.entity.Files;
import com.myteam.backupback.exception.CustomException;
import com.myteam.backupback.mapper.BackupMapper;
import com.myteam.backupback.mapper.FilesMapper;
import com.myteam.backupback.utils.CrcUtils;
import com.myteam.backupback.utils.TokenUtils;
import com.myteam.backupback.utils.ZipUtils;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import cn.hutool.core.date.DateUtil;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务处理
 **/

@Service
public class BackupService {
    @Resource
    private BackupMapper backupMapper;

    private static final Log log = LogFactory.get();
    @Autowired
    private FilesMapper filesMapper;

    /**
     * 新增
     */
    @Transactional
    public void upload(String backupName,String password, List<MultipartFile> files)  {
        // 获取当前用户
        AuthUser currentUser = TokenUtils.getCurrentUser();

        // 处理上传路径
        String uploadPath = Constants.PROJECT_PATH+ "/uploads/" + currentUser.getAccount();
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();}

        // 处理压缩包信息
        Backup curBackup = new Backup();
        curBackup.setBackupName(backupName);
        Date date = DateUtil.date().toSqlDate();
        curBackup.setCreatedAt(date);
        curBackup.setUserId(currentUser.getId());
        backupMapper.insert(curBackup);

        // 批量的每个文件
        List<Files> insertList = new ArrayList<>();
        List<File> compressedList = new ArrayList<>();
        for(MultipartFile file:files){
            String originalFilename = file.getOriginalFilename();
            long fileSize = file.getSize();
            if (originalFilename == null || originalFilename.isEmpty()) {
                continue; // 如果文件名为空跳过
            }
            try {
                File filePath = new File(uploadDir, originalFilename);
                File absoluteFile = filePath.getAbsoluteFile();
                compressedList.add(absoluteFile);
                file.transferTo(absoluteFile);

                String checksum = CrcUtils.calculateCRC32(absoluteFile);
                Files curFile = new Files();
                curFile.setFileName(originalFilename);
                curFile.setBackupId(curBackup.getId());
                curFile.setChecksum(checksum);
                curFile.setSize(fileSize);
                insertList.add(curFile);

            } catch (Exception e) {
                throw new CustomException(ResultCodeEnum.FILE_UPLOAD_ERROR);
            }
        }
        String zipName = curBackup.getId()+"--"+backupName+".zip";
        String zipPath = uploadPath+"/"+zipName;
        try {
            ZipUtils.compress(compressedList,zipPath,password);
            // 删除原始文件
            for (File file : compressedList) {
                if (!file.delete()) {
                    throw new CustomException(ResultCodeEnum.FILE_COMPRESS_ERROR);
                }
            }
        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.FILE_COMPRESS_ERROR);
        }


        filesMapper.batchInsert(insertList);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        backupMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            backupMapper.deleteById(id);
        }
    }

    /**
     * 根据ID查询
     */
    public Backup selectById(Integer id) {
        return backupMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Backup> selectAll(Backup backup) {

        return backupMapper.selectAll(backup);
    }

    /**
     * 分页查询
     */
    public PageInfo<Backup> selectPage(Backup backup, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Backup> list = backupMapper.selectAll(backup);
        return PageInfo.of(list);
    }


}
