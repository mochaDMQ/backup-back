package com.myteam.backupback.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZipUtils {
    private ZipUtils(){}

    public static void compress(File[] files, String cname, String pwd){
        ZipFile zipfile = new ZipFile(cname);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); // 压缩方式
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);   // 压缩级别
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 加密方式

    }

    public static File convertM2F(MultipartFile mfile) throws IOException {
        File tmp = File.createTempFile("tmp", null);
        try(FileOutputStream fos = new FileOutputStream(tmp)){
            fos.write(mfile.getBytes());
        }
        return tmp;
    }

}
