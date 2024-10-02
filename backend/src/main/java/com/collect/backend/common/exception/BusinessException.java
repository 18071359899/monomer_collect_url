package com.collect.backend.common.exception;

import lombok.Data;

/**
 * 封装业务异常
 */
@Data
public class BusinessException extends RuntimeException{
    protected Integer errCode;
    protected String  errMsg;
    public BusinessException(String errMsg){
        super(errMsg);
        this.errCode = CommonErrorEnum.BUSINESS_ERROR.getCode();
        this.errMsg = errMsg;
    }
    public BusinessException(Integer errCode,String errMsg){
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    public BusinessException(CommonErrorEnum errorEnum){
        super(errorEnum.getErrorMsg());
        this.errCode = errorEnum.getErrorCode();
        this.errMsg = errorEnum.getErrorMsg();
    }
}
