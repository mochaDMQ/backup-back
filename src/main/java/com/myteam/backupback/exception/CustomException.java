package com.myteam.backupback.exception;
import com.myteam.backupback.common.enums.ResultCodeEnum;

// 普通异常事件
public class CustomException extends RuntimeException{
    private String code;
    private String msg;


    // 通过预定义枚举
    public CustomException(ResultCodeEnum resultCodeEnum){
        this.code = resultCodeEnum.code;
        this.msg = resultCodeEnum.msg;
    }

    // 自定义
    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
