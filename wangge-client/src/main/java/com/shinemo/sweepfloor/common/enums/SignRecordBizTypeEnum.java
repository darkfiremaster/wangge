package com.shinemo.sweepfloor.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  打卡业务类型
 */
@Getter
@AllArgsConstructor
public enum SignRecordBizTypeEnum {
    SWEEP_FLOOR(1, "扫楼活动")

    ;
    private final int id;
    private final String desc;
}
