package com.shinemo.operate.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface OperateErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode PHONE_IS_NULL = new ErrorCode(500, "手机号为空");
}
