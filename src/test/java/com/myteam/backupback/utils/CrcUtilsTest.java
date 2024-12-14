package com.myteam.backupback.utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


class CrcUtilsTest {

    @Test
    void calculateCRC32_validFile() {
        String filePath = "src/test/resources/validCRC.txt";
        File file = new File(filePath);
        try {
            String crc32 = CrcUtils.calculateCRC32(file);
            System.out.println(crc32);
            assertNotNull(crc32);
            assertFalse(crc32.isEmpty());
            String expected = "F6E53B86".toLowerCase(Locale.ROOT);
            assertEquals(expected, crc32);
        } catch (IOException e) {
            fail("IOException was thrown");
        }
    }

    @Test
    void calculateCRC32_emptyFile() {
        String filePath = "src/test/resources/empty.txt";
        File file = new File(filePath);
        try {
            String crc32 = CrcUtils.calculateCRC32(file);
            assertEquals("0", crc32);
        } catch (IOException e) {
            fail("IOException was thrown");
        }
    }

    @Test
    void calculateCRC32_nonExistentFile() {
        String filePath = "D:\\nonexistent.txt";
        File file = new File(filePath);
        assertThrows(FileNotFoundException.class, () -> {
            CrcUtils.calculateCRC32(file);
        });
    }

}