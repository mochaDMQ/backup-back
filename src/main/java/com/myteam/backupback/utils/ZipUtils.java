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

    public static void compress(List<File> files, String cname, String pwd) throws ZipException {
        ZipFile zipFile = new ZipFile(cname,pwd.toCharArray());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); // 压缩方式
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);   // 压缩级别
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 加密方式

        for(File file:files){
            zipFile.addFile(file,zipParameters);
        }
    }
}
