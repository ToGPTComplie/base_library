package com.example.library.exception;

import com.example.library.common.Result;
import com.example.library.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public Result<?> authException(AuthException e) {
        log.warn("认证异常: {}", e.getMessage());
        return Result.error(e.getResultCode(), e.getErrorData());
    }

    @ExceptionHandler(CommonException.class)
    public Result<?> commonException(CommonException e) {
        log.warn("通用异常: {}", e.getMessage());
        return Result.error(e.getResultCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidateException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("参数校验失败: {}", e.getMessage());
        return Result.error(ResultCode.VALIDATE_FAILED, errors);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("未处理的异常: {}", e.getMessage(), e);
        return Result.error();
    }
}
