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
     * 新增
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("backupName")String backupName,
                         @RequestParam("password")String password,
                         @RequestParam("file") List<MultipartFile> files) {
        backupService.upload(backupName,password,files);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        backupService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        backupService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
//    @PutMapping("/update")
//    public Result updateById(@RequestBody Backup backup) {
//        backupService.updateById(backup);
//        return Result.success();
//    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Backup backup = backupService.selectById(id);
        return Result.success(backup);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Backup backup ) {
        List<Backup> list = backupService.selectAll(backup);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Backup backup,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Backup> page = backupService.selectPage(backup, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/showDetails/{id}")
    public Result showDetails(@PathVariable Integer id){
        List<Files> files = filesService.selectByBackupId(id);
        return Result.success(files);
    }

}
