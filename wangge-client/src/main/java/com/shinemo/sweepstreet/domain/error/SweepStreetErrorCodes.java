package com.shinemo.sweepstreet.domain.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface SweepStreetErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode VISIT_RECORDING_NOT_EXIST = new ErrorCode(502, "走访记录不存在或被删除！");

    ErrorCode DATE_PARSE_ERROR = new ErrorCode(504, "请求参数解析错误！");

}
