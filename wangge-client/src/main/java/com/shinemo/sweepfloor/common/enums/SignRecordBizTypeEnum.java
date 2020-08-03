package com.shinemo.sweepfloor.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  打卡业务类型
 */
@Getter
@AllArgsConstructor
public enum SignRecordBizTypeEnum {
    SWEEP_FLOOR(1, "扫楼活动"),
    STALL_UP(2, "摆摊活动"),
    SWEEP_VILLAGE(3, "扫村活动"),
    GROUP_SERVICE_DAY(4,"集团服务日")

    ;
    private final int id;
    private final String desc;
}
