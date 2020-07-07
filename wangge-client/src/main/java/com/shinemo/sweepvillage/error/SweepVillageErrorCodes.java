package com.shinemo.sweepvillage.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface SweepVillageErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode VISIT_RECORDING_NOT_EXIST = new ErrorCode(502, "走访记录不存在或被删除！");
    ErrorCode VISIT_RECORDING_UPDATE_NOT_AUTH = new ErrorCode(503, "权限不足！");
    ErrorCode NOT_GRID_USER = new ErrorCode(501, "非网格用户！");
    ErrorCode SWEEP_VILLAGE_ACTIVITY_NOT_EXIST = new ErrorCode(1001, "扫村活动不存在！");
    ErrorCode SWEEP_VILLAGE_STATUS_ERROR = new ErrorCode(1002, "扫村活动状态错误！");
    ErrorCode SWEEP_VILLAGE_ACTIVITY_PROCESSING_EXIST = new ErrorCode(1003, "已存在进行中的扫村活动！");

    ErrorCode DATE_PARSE_ERROR = new ErrorCode(504, "请求参数解析错误！");

}
