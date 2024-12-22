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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import cn.hutool.core.date.DateUtil;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
     * 上传备份
     */
    @Transactional
    public void upload(String backupName, String password, List<MultipartFile> files) {
        // 获取当前用户
        AuthUser currentUser = TokenUtils.getCurrentUser();
        // 处理上传路径
        File userPath = getUserPath();
        if (!userPath.exists()) {
            userPath.mkdirs();
        }

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
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            long fileSize = file.getSize();
            if (originalFilename == null || originalFilename.isEmpty()) {
                continue; // 如果文件名为空跳过
            }
            try {
                File filePath = new File(userPath, originalFilename);
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
                log.error("文件保存失败: {}", originalFilename, e);
                throw new CustomException(ResultCodeEnum.FILE_UPLOAD_ERROR);
            }
        }
        String zipName = curBackup.getId() + "--" + backupName + ".zip";
        File zipFile = new File(userPath, zipName);
        try {
            ZipUtils.compress(compressedList, zipFile, password);
            // 删除原始文件
            for (File file : compressedList) {
                if (!file.delete()) {
                    log.error("文件删除失败:{}", file.getName());
                    throw new CustomException(ResultCodeEnum.FILE_COMPRESS_ERROR);
                }
            }
        } catch (Exception e) {
            log.error(e);
            throw new CustomException(ResultCodeEnum.FILE_COMPRESS_ERROR);
        }
        filesMapper.batchInsert(insertList);
    }

    @Transactional
    public void download(Integer id, String pwd, HttpServletResponse response) {
        Backup curBackup = backupMapper.selectById(id);
        if (curBackup == null) {
            throw new CustomException(ResultCodeEnum.FILERECORD_NOT_EXIST);
        }
        File userPath = getUserPath();
        File zipFile = getZipFile(curBackup, userPath);
        File tempFolder = new File(userPath + "/" + curBackup.getBackupName());

        if (!zipFile.exists()) {
            throw new CustomException(ResultCodeEnum.FILE_NOT_EXIST);
        }

        ZipUtils.decompress(zipFile, tempFolder, pwd);

        // 校验逻辑
        List<Files> fileRecords = filesMapper.selectByBackupId(id);
        List<File> successList = new ArrayList<>();
        List<File> failureList = new ArrayList<>();
        for (File file : tempFolder.listFiles()) {
            try {
                String checksum = CrcUtils.calculateCRC32(file);
                boolean isValid = fileRecords.stream().anyMatch(
                        record -> record.getFileName()
                                .equals(file.getName()) && record.getChecksum().equals(checksum));
                if (isValid) {
                    successList.add(file);
                } else {
                    failureList.add(file);
                }
            } catch (Exception e) {
                failureList.add(file);
            }
        }
        // 如果有校验错误
        if (!failureList.isEmpty()) {
            File errFiles = new File(tempFolder,"errMessage.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(errFiles))) {
                writer.write("以下文件信息校验错误：");
                writer.newLine();
                for (File errFile : failureList) {
                    writer.write(errFile.getName());
                    writer.newLine();
                }
                successList.add(errFiles);
            } catch (IOException e) {
                throw new CustomException(ResultCodeEnum.ERRFILE_RECORD);
            }
        }
        // 重新压缩校验成功的文件
        String newName = curBackup.getBackupName() + ".zip";
        File newZipFile = new File(userPath, newName);

        try {
            ZipUtils.compress(successList, newZipFile, null);
        } catch (ZipException e) {
            throw new CustomException(ResultCodeEnum.FILE_DOWNLOAD_ERROR);
        } finally {
            for (File file : tempFolder.listFiles()) {
                if (!file.delete()) {
                    log.warn("临时文件删除失败: {}", file.getName());
                }
            }
            if (!tempFolder.delete()) {
                log.warn("临时文件夹删除失败: {}", tempFolder.getName());
            }
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + newZipFile.getName());
        try (FileInputStream fis = new FileInputStream(newZipFile);
             ServletOutputStream outputStream = response.getOutputStream()) {
            // 文件写入响应流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

        } catch (IOException e) {
            throw new CustomException(ResultCodeEnum.FILE_DOWNLOAD_ERROR);
        } finally {
            newZipFile.delete();
        }
    }

    /**
     * 删除备份
     */
    @Transactional
    public void deleteById(Integer id) {
        Backup curBackup = backupMapper.selectById(id);
        if (curBackup == null) {
            throw new IllegalArgumentException("备份包" + id + "记录不存在");
        }
        backupMapper.deleteById(id);

        File userPath = getUserPath();
        File zipFile = getZipFile(curBackup, userPath);
        if (zipFile.exists() && !zipFile.delete()) {
            throw new RuntimeException("备份包删除失败，可能已从服务端移除");
        }
    }


    /**
     * 根据ID查询
     */
    public Backup selectById(Integer id) {
        return backupMapper.selectById(id);
    }


    /**
     * 查询特定用户所有备份
     */
    public List<Backup> selectAll() {
        AuthUser currentUser = TokenUtils.getCurrentUser();
        Integer userId = currentUser.getId();
        return backupMapper.selectAll(userId);
    }

    private File getUserPath() {
        // 获取当前用户
        AuthUser currentUser = TokenUtils.getCurrentUser();
        // 处理上传路径
        String uploadPath = Constants.PROJECT_PATH + "/uploads/" + currentUser.getAccount();
        return (new File(uploadPath));
    }

    private File getZipFile(Backup backup, File userPath) {
        String zipName = backup.getId() + "--" + backup.getBackupName() + ".zip";
        return (new File(userPath, zipName));
    }


}
