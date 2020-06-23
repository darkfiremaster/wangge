package com.shinemo.auth;

import com.shinemo.client.common.ErrorInfo;


public interface AuthErrors {

    // 业务异常
    ErrorInfo TOKEN_ERROR = new ErrorInfo(10000L, "TOKEN_ERROR", "TOKEN验证失效");
    ErrorInfo SERVICE_ERROR = new ErrorInfo(10001L, "SERVICE_ERROR", "服务异常");

}
