package com.shinemo.wangge.core.service.groupserviceday;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceListRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayFinishedVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayVO;

import java.util.List;
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
    ApiResult<List<GroupDO>> getLatestMarketingGroupList();

    /**
     * 获取已结束办理量、活动次数
     */
    ApiResult<GroupServiceDayFinishedVO> getFinishedCount(Integer type);

    /**
     * 获取活动列表
     */
    ApiResult<ListVO<GroupServiceDayVO>> getActivityListByStatus(GroupServiceListRequest request);

    /**
     * 开始打卡
     */
    ApiResult<Void> startSign(GroupServiceDaySignRequest request);

    /**
     * 结束打卡
     */
    ApiResult<Void> endSign(GroupServiceDaySignRequest request);

    /**
     * 活动取消
     */
    ApiResult<Void> cancel(Long id);

    /**
     * 获取参与人列表
     */
    ApiResult<Map<String, Object>> getPartnerList(Map<String,Object> requestData);

    /**
     * 自动结束活动
     */
    ApiResult<Void> autoEnd(GroupServiceDayDO serviceDayDO);

}
