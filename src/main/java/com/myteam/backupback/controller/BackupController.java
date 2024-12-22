package com.myteam.backupback.controller;

import com.github.pagehelper.PageInfo;
import com.myteam.backupback.common.Result;
import com.myteam.backupback.entity.Backup;
import com.myteam.backupback.entity.Files;
import com.myteam.backupback.service.BackupService;
import com.myteam.backupback.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 压缩包信息接口
 **/

@RestController
@RequestMapping("/backup")
public class BackupController {

    @Resource
    private BackupService backupService;


    @Resource
    private FilesService filesService;

    /**
     * 上传备份
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("backupName") String backupName,
                         @RequestParam("password") String password,
                         @RequestParam("file") List<MultipartFile> files) {
        backupService.upload(backupName, password, files);
        return Result.success();
    }

    /**
     * 下载备份
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Integer id,
                         @RequestParam("password") String password,
                         HttpServletResponse response) {
        System.out.println(password);
        backupService.download(id, password,response);
    }

    /**
     * 删除
     */
    @GetMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        backupService.deleteById(id);
        return Result.success();
    }


    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Backup backup = backupService.selectById(id);
        return Result.success(backup);
    }

    /**
     * 查询特定用户所有备份
     */
    @GetMapping("/selectAll")
    public Result selectAll(Backup backup) {
        List<Backup> list = backupService.selectAll();
        return Result.success(list);
    }

    /**
     * 查询备份包下所有文件
     */
    @GetMapping("/showDetails/{id}")
    public Result showDetails(@PathVariable Integer id) {
        List<Files> files = filesService.selectByBackupId(id);
        return Result.success(files);
    }

}
