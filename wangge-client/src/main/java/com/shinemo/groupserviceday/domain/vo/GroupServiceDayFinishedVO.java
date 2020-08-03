package com.shinemo.groupserviceday.domain.vo;

import lombok.Data;

/**
 * zz
 */
@Data
public class GroupServiceDayFinishedVO {
    /** 活动id */
    private Long id;
    /** 活动次数 */
    private Integer activityCount;
    /** 办理量次数 */
    private Integer businessCount;
}
