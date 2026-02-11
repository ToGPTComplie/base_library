package com.example.library.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private T data;
    private String message;
    private Integer code;

    Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    public static <T> Result<T> success() {
        return new Result<T>(ResultCode.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>(ResultCode.SUCCESS);
        result.data = data;
        return result;
    }

    public static <T> Result<T> error() {
        return new Result<T>(ResultCode.FAILED);
    }

    public static <T> Result<T> error(T data) {
        Result<T> result = new Result<T>(ResultCode.FAILED);
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode);
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        Result<T> result = new Result<>(resultCode);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode, T data) {
        Result<T> result = new Result<T>(resultCode);
        result.data = data;
        return result;
    }
}
