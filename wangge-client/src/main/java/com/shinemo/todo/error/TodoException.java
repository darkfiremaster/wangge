package com.shinemo.todo.error;

import com.shinemo.common.tools.exception.ErrorCode;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 17:08
 * @Desc
 */
public class TodoException extends RuntimeException {
    public Integer code;

    public TodoException() {
    }

    public TodoException(ErrorCode errorCode) {
        this.code = errorCode.code;
    }
}
