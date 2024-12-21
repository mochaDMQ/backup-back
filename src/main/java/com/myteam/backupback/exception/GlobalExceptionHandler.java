package com.myteam.backupback.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.myteam.backupback.common.Result;
import com.myteam.backupback.common.enums.ResultCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages="com.myteam.backupback.controller")
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get();

    @ExceptionHandler(Exception.class)
    @ResponseBody// 返回json串
    public Result error(HttpServletRequest request, Exception e){
        log.error("异常信息：",e);
        return Result.error();
    }

    // 普通异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody//返回json串
    public Result customError(HttpServletRequest request, CustomException e){
        log.error("异常信息：",e);
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMaxSizeException(HttpServletRequest request,MaxUploadSizeExceededException e) {
        return Result.error(ResultCodeEnum.FILE_EXCEED_MAXSIZE);
    }
}
