package com.shinemo.wangge.core.service.groupserviceday;

import com.shinemo.common.tools.result.ApiResult;

/**
 * @Author shangkaihui
 * @Date 2020/8/3 11:37
 * @Desc
 */
public interface GroupServiceDayService {

    /**
     * 获取集团列表
     */
    ApiResult getGroupList();

    /**
     * 新建集团服务日
     */
    ApiResult createGroupServiceDay();


    /**
     * 获取最近营销的集团列表
     */
    ApiResult getLatestMarketingGroupList();
}
