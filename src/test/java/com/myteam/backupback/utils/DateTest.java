package com.myteam.backupback.utils;

import cn.hutool.core.date.DateUtil;
import java.util.Date;

public class DateTest {
    public static void main(String[] args) {
        Date date = DateUtil.date();
        System.out.println(date);
    }
}
