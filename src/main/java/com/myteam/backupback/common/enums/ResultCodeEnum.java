package com.myteam.backupback.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "成功"),

    PARAM_ERROR("400", "参数异常"),
    TOKEN_INVALID_ERROR("401", "无效的token"),
    TOKEN_CHECK_ERROR("401", "token验证失败，请重新登录"),
    PARAM_LOST_ERROR("4001", "参数缺失"),

    SYSTEM_ERROR("500", "系统异常"),
    USER_EXIST_ERROR("5001", "账号已存在"),
    USER_NOT_LOGIN("5002", "用户未登录"),
    USER_ACCOUNT_ERROR("5003", "账号或密码错误"),
    USER_NOT_EXIST_ERROR("5004", "用户不存在"),
    PARAM_PASSWORD_ERROR("5005", "原密码输入错误"),
    FILE_UPLOAD_ERROR("5006", "备份上传失败"),
    FILE_COMPRESS_ERROR("5007", "备份压缩失败"),
    FILE_EXCEED_MAXSIZE("5008", "请上传总量不超过1GB的数据"),
    FILE_DELETE_ERROR("5009", "备份删除失败"),
    FILERECORD_NOT_EXIST("5010", "备份记录不存在"),
    DATABASE_OPERATION_FAILED("5011","数据库操作失败"),
    FILE_NOT_EXIST("5012", "备份不存在"),
    FILE_DOWNLOAD_ERROR("5013", "文件下载失败"),
    ERRFILE_RECORD("5014","创建错误信息文件失败");



    public final String code;
    public final String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
