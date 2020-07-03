package com.shinemo.sweepvillage.domain.query;

import com.shinemo.common.tools.query.Query;
import lombok.Data;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
public class SweepVillageMarketingNumberQuery extends Query {
    private Long id;
    private String mobile;
    /**
     * 扫村活动ID
     */
    private Long activityId;
}
