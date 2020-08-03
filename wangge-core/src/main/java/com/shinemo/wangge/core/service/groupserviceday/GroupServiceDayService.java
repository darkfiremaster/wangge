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

    /**
     * 获取已结束办理量、活动次数
     */
    ApiResult getFinishedCount();

    /**
     * 获取活动列表
     */
    ApiResult getActivityListByStatus();

    /**
     * 开始打卡
     */
    ApiResult startSign();

    /**
     * 结束打卡
     */
    ApiResult endSign();

    /**
     * 活动取消
     */
    ApiResult cancel();

    /**
     * 获取参与人列表
     */
    ApiResult getPartnerList();

}
