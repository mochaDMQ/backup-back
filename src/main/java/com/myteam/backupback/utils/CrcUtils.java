package com.myteam.backupback.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

// 后端生成CRC校验
public final class CrcUtils {
    private CrcUtils(){}
    public static String calculateCRC32(File file) throws IOException {
        CRC32 crc = new CRC32();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                crc.update(buffer, 0, len);
            }
        }
        return Long.toHexString(crc.getValue());
    }
}
