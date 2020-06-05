package com.shinemo.sweepvillage.error;

import com.shinemo.common.tools.exception.ErrorCode;

public interface SweepVillageErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode VISIT_RECORDING_NOT_EXIST = new ErrorCode(500, "走访记录不存在或被删除！");
    ErrorCode SWEEP_VILLAGE_NOT_EXIST = new ErrorCode(500, "扫村计划不存在或已被删除！");
    ErrorCode VISIT_RECORDING_UPDATE_NOT_AUTH = new ErrorCode(500, "只有录入本人或对应网格长才可以编辑！");
}
