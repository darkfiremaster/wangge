package com.shinemo.sweepfloor.domain.query;

import lombok.Data;

import java.util.List;

@Data
public class SweepFloorMarketingNumberQuery {
    private Long id;
    /** 活动id */
    private Long activityId;
    /** 用户id */
    private String userId;
    /** 活动id集合 */
    private List<Long> activityIds;
}
