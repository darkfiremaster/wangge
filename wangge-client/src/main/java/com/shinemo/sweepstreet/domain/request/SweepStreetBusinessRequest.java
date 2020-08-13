package com.shinemo.sweepstreet.domain.request;

import com.shinemo.client.common.BaseDO;
import lombok.Data;

/**
 * 类说明: 扫街业务办理请求
 *
 * @author zengpeng
 */
@Data
public class SweepStreetBusinessRequest extends BaseDO {

    /**
     * 扫街活动id
     */
    private Long activityId;
}
