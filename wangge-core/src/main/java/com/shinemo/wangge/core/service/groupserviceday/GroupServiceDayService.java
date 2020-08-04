package com.shinemo.wangge.core.service.groupserviceday;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;

import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/8/3 11:37
 * @Desc
 */
public interface GroupServiceDayService {

    /**
     * 获取集团列表
     */
    ApiResult<Map<String, Object>> getGroupList(String groupName);

    /**
     * 新建集团服务日
     */
    ApiResult<Void> createGroupServiceDay(GroupServiceDayRequest groupServiceDayRequest);


    /**
     * 获取最近营销的集团列表
     */
    ApiResult getLatestMarketingGroupList();

    /**
     * 获取已结束办理量、活动次数
     */
    ApiResult getFinishedCount(Integer type);

    /**
     * 获取活动列表
     */
    ApiResult getActivityListByStatus();

    /**
     * 开始打卡
     */
    ApiResult startSign(GroupServiceDaySignRequest request);

    /**
     * 结束打卡
     */
    ApiResult endSign(GroupServiceDaySignRequest request);

    /**
     * 活动取消
     */
    ApiResult cancel(Long id);

    /**
     * 获取参与人列表
     */
    ApiResult getPartnerList(GroupServiceDayPartnerListRequest request);

}
