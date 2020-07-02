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
    END(2, "已结束"),
    CANCEL(3, "参与人取消计划"),
    ABNORMAL_END(4, "异常签退：超出打卡范围"),
    DELETE(-1, "删除"),
    ;
    private final int id;
    private final String desc;
}
