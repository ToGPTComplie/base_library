package com.example.library.exception;

import com.example.library.common.ResultCode;
import lombok.Getter;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AuthException extends RuntimeException {
    private ResultCode resultCode;
    private Map<String, String> errorData;

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.errorData = new HashMap<>();
    }

    public AuthException(ResultCode resultCode, Map<String, String> errorData) {
        super(resultCode.getMessage() + ": " + errorData);
        this.resultCode = resultCode;
        this.errorData = errorData;
    }
}
