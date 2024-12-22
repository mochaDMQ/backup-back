package com.myteam.backupback.utils;

import net.lingala.zip4j.exception.ZipException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZipUtilsTest {

    @Test
    void testCompress() throws ZipException {
       File folder = new File("src/test/resources/ZipTest/pack2");
       List<File> files = Arrays.asList(folder.listFiles());
       File zipFile = new File("src/test/resources/ZipTest/pack2.zip");
       ZipUtils.compress(files,zipFile,"123");
       assertTrue(zipFile.exists());
    }

    @Test
    void decompress() {
        File zipFile = new File("src/test/resources/ZipTest/正确的zip/pack1.zip");
        File target = new File("src/test/resources/ZipTest");
        String pwd = "123";
        ZipUtils.decompress(zipFile,target,pwd);

    }
}