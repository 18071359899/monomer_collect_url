package com.collect.backend.common.exception;


import com.collect.backend.common.BaseResponse;
import com.collect.backend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获器，捕获完后返回规定格式给前端，比如提示错误信息
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 校验实体类异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResponse<?> MethodArgumentNotValidException(MethodArgumentNotValidException e){
        StringBuilder stringBuilder = new StringBuilder();
        e.getBindingResult().getFieldErrors().
                forEach(x->stringBuilder.append(x.getDefaultMessage()).append(","));
        String s = stringBuilder.toString();
        return ResultUtils.error(CommonErrorEnum.PARAM_INVALID.getCode(),s.substring(0,s.length()-1));
    }
    /**
     *          全局业务异常捕获器
     */
    @ExceptionHandler(value = BusinessException.class)
    public BaseResponse<?> businessExption(BusinessException e){
        log.info("business exception! The reason is :{}",e.getMessage());
        return ResultUtils.error(e.getErrCode(),e.getErrMsg());
    }
    /**
    *          最后一道防线，防止把错误返回给前端
    */
    @ExceptionHandler(value = Throwable.class)
    public BaseResponse<?> throwAble(Throwable e){
        log.error("system exception! The reason is :{}",e.getMessage(),e);
        return ResultUtils.error(CommonErrorEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public BaseResponse<?> AuthenticationExceptionHanlder(Throwable e){
        log.error("用户名或密码有误! The reason is :{}",e.getMessage(),e);
        return ResultUtils.error(CommonErrorEnum.USERNAME_OR_PASSWORD_FAIL);
    }
}
