package com.example.library.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂无登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),

    USER_ALREADY_EXIST(1001, "用户已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),

    BOOK_ALREADY_EXIST(40001,"图书已经存在"),
    BOOK_NOT_FOUND(40002,"图书不存在"),
    BOOK_AVAILABLE_STOCK_NOT_ENOUGH(40003,"图书库存不足"),

    THIS_BOOK_ALREADY_BORROWED(41001,"您已借阅该书且未归还"),
    YOU_CANT_BORROW_MORE(41002,"您已借阅了最大数量的书籍，无法借阅更多");

    private final Integer code;
    private final String message;


}
