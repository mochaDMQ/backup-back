package com.myteam.backupback.service;

import com.myteam.backupback.entity.Backup;
import com.myteam.backupback.mapper.BackupMapper;
import com.myteam.backupback.service.BackupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BackupServiceTest {

    @Mock
    private BackupMapper backupMapper;

    @InjectMocks
    private BackupService backupService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


}