package com.shinemo.wangge.core.service.groupserviceday;

import com.shinemo.common.tools.result.ApiResult;

public interface GroupSerDayRedirctService {
    /**
     * 获取跳转企业信息url
     * @param activityId
     * @return
     */
    ApiResult<String> getRedirctGrouSerUrl(String activityId);

    ApiResult<String> getRedirctSmsHotUrl(Long activityId);
}
