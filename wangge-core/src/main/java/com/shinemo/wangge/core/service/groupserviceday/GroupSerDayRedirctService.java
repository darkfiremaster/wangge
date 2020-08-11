package com.shinemo.wangge.core.service.groupserviceday;

import com.shinemo.common.tools.result.ApiResult;

public interface GroupSerDayRedirctService {
    ApiResult<String> getRedirctGrouSerUrl(Long activityId);
}
