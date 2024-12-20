package com.myteam.backupback.service;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myteam.backupback.common.Constants;
import com.myteam.backupback.common.Result;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import com.myteam.backupback.common.enums.RoleEnum;
import com.myteam.backupback.entity.Backup;
import com.myteam.backupback.exception.CustomException;
import com.myteam.backupback.mapper.BackupMapper;
import com.myteam.backupback.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 业务处理
 **/

@Service
public class BackupService {
    @Resource
    private BackupMapper backupMapper;

    private static final Log log = LogFactory.get();
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 新增
     */
    public void upload(String backupName,String password, List<MultipartFile> files) {
       log.info(backupName+password);
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 如果目录不存在，则创建
        }
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            log.info(originalFilename);
            if (originalFilename == null || originalFilename.isEmpty()) {
                continue; // 如果文件名为空，跳过
            }
            try {
                // 构造目标文件路径
                File filePath = new File(UPLOAD_DIR, originalFilename);
                File absoluteFile = filePath.getAbsoluteFile();
                // 保存文件到目标路径
                file.transferTo(absoluteFile);
                System.out.println("文件保存成功：" + filePath.getAbsolutePath());
            } catch (IOException e) {
                log.error(e);
            }
        }


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

//    /**
//     * 修改
//     */
//    public void updateById(Backup backup) {
//        backupMapper.updateById(backup);
//    }

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
