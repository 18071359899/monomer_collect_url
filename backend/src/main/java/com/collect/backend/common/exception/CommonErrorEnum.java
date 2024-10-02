package com.collect.backend.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum{
    BUSINESS_ERROR(400,"{0}"),  //业务异常
    SYSTEM_ERROR(401,"系统出小差喽，请稍后在试哦~"),
    USERNAME_OR_PASSWORD_FAIL(401,"用户名或密码有误"),
    PARAM_INVALID(402,"参数校验失败"),
    LOCK_LIMIT(403,"请求太频繁了，请稍后再试~");

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
