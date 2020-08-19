package com.shinemo.wangge.core.service.sweepstreet;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepstreet.domain.request.SweepStreetBusinessRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMarketNumberVO;

/**
 * 类说明: 扫街活动业务
 *
 * @author zengpeng
 */
public interface SweepStreetMarketService {


    /**
     * 获取指定活动id的业务列表
     * @param activityId
     * @return
     */
    ApiResult<SweepStreetMarketNumberVO> getByActivityId(Long activityId);

    /**
     * 录入业务办理量
     * @param request
     * @return
     */
    ApiResult enterMarketingNumber(SweepStreetBusinessRequest request);

}