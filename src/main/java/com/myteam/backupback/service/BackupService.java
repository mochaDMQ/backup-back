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
                log.error("文件保存失败: {}", originalFilename,e);
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
                    log.error("文件删除失败:{}",file.getName());
                    throw new CustomException(ResultCodeEnum.FILE_COMPRESS_ERROR);
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new CustomException(ResultCodeEnum.FILE_COMPRESS_ERROR);
        }
        filesMapper.batchInsert(insertList);
    }

    /**
     * 删除
     */
    @Transactional
    public void deleteById(Integer id) {
        AuthUser currentUser = TokenUtils.getCurrentUser();
        // 构建zip路径

        String uploadPath = Constants.PROJECT_PATH+ "/uploads/" + currentUser.getAccount();
        Backup del = backupMapper.selectById(id);
        if(del == null){
            throw new CustomException(ResultCodeEnum.FILE_NOT_EXIST);
        }

        String zipName = del.getId()+"--"+del.getBackupName()+".zip";
        File zipFile = new File(uploadPath,zipName);
        if(!zipFile.delete()){
            throw new CustomException(ResultCodeEnum.FILE_DELETE_ERROR);
        }

        try {
            int rowsAffected = backupMapper.deleteById(id);
            if (rowsAffected == 0) {
                log.error("删除备份记录删除错误");
                throw new CustomException(ResultCodeEnum.DATABASE_OPERATION_FAILED);
            }
        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.DATABASE_OPERATION_FAILED);
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
        AuthUser currentUser = TokenUtils.getCurrentUser();
        Integer userId = currentUser.getId();
        return backupMapper.selectAll(backup,userId);
    }

    /**
     * 分页查询
     */
//    public PageInfo<Backup> selectPage(Backup backup, Integer pageNum, Integer pageSize) {
//        PageHelper.startPage(pageNum, pageSize);
//        List<Backup> list = backupMapper.selectAll(backup);
//        return PageInfo.of(list);
//    }


}
