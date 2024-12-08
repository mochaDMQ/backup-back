package com.myteam.backupback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.myteam.backupback.mapper")
public class BackupBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackupBackApplication.class, args);
    }

}
