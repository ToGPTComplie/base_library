package com.example.library.exception;

import com.example.library.common.ResultCode;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private ResultCode resultCode;

    public CommonException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
