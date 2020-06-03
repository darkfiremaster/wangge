package com.shinemo.sweepvillage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  扫村活动状态
 */
@Getter
@AllArgsConstructor
public enum SweepVillageStatusEnum {
    NOT_START(0, "未开始"),
    PROCESSING(1, "进行中"),
    END(2, "已结束");
    ;
    private final int id;
    private final String desc;
}
