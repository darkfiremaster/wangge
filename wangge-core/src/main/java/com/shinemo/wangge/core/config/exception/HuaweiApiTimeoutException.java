package com.shinemo.wangge.core.config.exception;

import com.shinemo.common.tools.exception.ErrorCode;
import lombok.Getter;

/**
 * @Author shangkaihui
 * @Date 2020/7/9 09:26
 * @Desc
 */
@Getter
public class HuaweiApiTimeoutException extends RuntimeException {

    private Integer code = 500;

    public HuaweiApiTimeoutException(ErrorCode errorCode) {
        super(errorCode.msg);
        this.code = errorCode.code;
    }


    public HuaweiApiTimeoutException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
