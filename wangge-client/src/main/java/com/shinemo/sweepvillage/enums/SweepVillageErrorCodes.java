package com.shinemo.sweepvillage.enums;

import com.shinemo.common.tools.exception.ErrorCode;

public interface SweepVillageErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode NOT_GRID_USER = new ErrorCode(501, "非网格用户！");
    ErrorCode SWEEP_VILLAGE_ACTIVITY_NOT_EXIST = new ErrorCode(1001, "扫村活动不存在！");
    ErrorCode SWEEP_VILLAGE_STATUS_ERROR = new ErrorCode(1002, "扫村活动状态错误！");
}
