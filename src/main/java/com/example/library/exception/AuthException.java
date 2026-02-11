package com.example.library.exception;

import com.example.library.common.ResultCode;
import lombok.Getter;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Getter
public class AuthException extends RuntimeException {
    private String detailMessage;
    private ResultCode resultCode;

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public AuthException(ResultCode resultCode, String detailMessage) {
        super(resultCode.getMessage() + ": " + detailMessage);
        this.resultCode = resultCode;
        this.detailMessage = detailMessage;
    }
}
