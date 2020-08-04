package com.shinemo.groupserviceday.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface GroupServiceDayErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");

    ErrorCode ACTIVITY_NOT_EXIT = new ErrorCode(500, "集团服务日活动不存在");


}
