package com.myteam.backupback.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.util.List;

public class ZipUtils {
    private ZipUtils(){}

    public static void compress(List<File> files, File cname, String pwd) throws ZipException {
        // 如果密码为空或为null，则不启用加密
        boolean enableEncryption = pwd != null && !pwd.isEmpty();
        ZipFile zipFile = enableEncryption ? new ZipFile(cname, pwd.toCharArray()) : new ZipFile(cname);

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); // 压缩方式
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);   // 压缩级别

        if (enableEncryption) {
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 加密方式
        }

        for(File file:files){
            zipFile.addFile(file,zipParameters);
        }
    }

    public static void decompress(File zipFile, File targetFolder, String pwd){
        try {
            // 创建 ZipFile 实例
            ZipFile zip = new ZipFile(zipFile, pwd.toCharArray());
            // 验证是否需要密码且密码是否正确
            if (!zip.isValidZipFile()) {
                throw new ZipException("文件不是有效的 ZIP 文件或密码错误");
            }
            zip.extractAll(targetFolder.getAbsolutePath());
        } catch (ZipException e) {
            System.err.println("解压失败: " + e.getMessage());
        }
    }
}
