package com.shinemo.wangge.core.service.groupserviceday;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayMarketNumberVO;

/**
 *
 * @author zengpeng
 */
public interface GroupServiceDayMarketingNumberService {

    /**
     * 获取指定活动id的业务列表
     * @param activityId
     * @return
     */
    ApiResult<GroupServiceDayMarketNumberVO> getByActivityId(Long activityId);
}
