package com.shinemo.sweepfloor.common.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface SweepFloorErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode SWEEP_FLOOR_ACTIVITY_NOT_EXIST = new ErrorCode(500, "扫楼活动不存在！");
    ErrorCode EXIST_PROCESSING_SWEEP_FLOOR = new ErrorCode(500, "已存在进行中的扫楼活动！");
    ErrorCode SWEEP_FLOOR_STATUS_ERROR_START_SIGN = new ErrorCode(500, "该扫楼活动不可进行开始打卡操作！");
    ErrorCode SWEEP_FLOOR_SIGN_DISTANCE_ERROR = new ErrorCode(500, "当前位置不在打卡范围！");
    ErrorCode SWEEP_FLOOR_STATUS_ERROR_END_SIGN = new ErrorCode(500, "该扫楼活动不可进行结束打卡操作！");
    ErrorCode SWEEP_FLOOR_STATUS_ERROR = new ErrorCode(500, "扫楼活动状态错误！");
    ErrorCode SWEEP_FLOOR_BUILDING_NAME_DUPLICATE = new ErrorCode(500, "楼栋名已存在！");
    ErrorCode SWEEP_FLOOR_HOUSE_NUMBER_DUPLICATE = new ErrorCode(302, "门牌号已存在！");
    ErrorCode NOT_GRID_USER = new ErrorCode(500, "非网格用户！");
}
