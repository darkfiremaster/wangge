package com.shinemo.sweepstreet.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface SweepStreetErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");

    ErrorCode ACTIVITY_NOT_EXIT = new ErrorCode(500, "扫街活动不存在");
    ErrorCode STARTED_ACTIVITY_EXIT = new ErrorCode(500, "已存在进行中的扫街活动！");
    ErrorCode ACTIVITY_START_ERROR = new ErrorCode(500, "当前活动不可以进行打卡操作！");
    ErrorCode GROUP_SERVICE_SIGN_DISTANCE_ERROR = new ErrorCode(500, "当前位置不在打卡范围！");
    ErrorCode ACTIVITY_END_ERROR = new ErrorCode(500, "当前活动不可以进行签离操作！");
    ErrorCode AUTH_ERROR = new ErrorCode(500, "权限不足！");
    ErrorCode ACTIVITY_SEARCH_ERROR = new ErrorCode(500, "子活动数查询异常！");


}
