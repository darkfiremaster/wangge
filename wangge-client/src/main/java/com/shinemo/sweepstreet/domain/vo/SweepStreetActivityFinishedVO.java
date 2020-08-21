package com.shinemo.sweepstreet.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SweepStreetActivityFinishedVO {

    /** 活动次数 */
    private Integer activityCount;
    /** 走访商户数量 */
    private Integer businessCount;
}
