package com.shinemo.wangge.core.service.groupserviceday.impl;

import cn.hutool.core.util.StrUtil;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayMarketingNumberDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayMarketingNumberQuery;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayFinishedVO;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMarketingNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shangkaihui
 * @Date 2020/8/3 11:37
 * @Desc
 */
@Service
@Slf4j
public class GroupServiceDayServiceImpl implements GroupServiceDayService {

    @Resource
    private ThirdApiMappingService thirdApiMappingService;
    @Resource
    private GroupServiceDayMapper groupServiceDayMapper;
    @Resource
    private GroupServiceDayMarketingNumberMapper groupServiceDayMarketingNumberMapper;


    @Override
    public ApiResult<Map<String, Object>> getGroupList(String groupName) {
        //透传华为
        HashMap<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(groupName)) {
            map.put("groupName", groupName);
        }
        map.put("mobile", SmartGridContext.getMobile());
        //todo 修改apiName
        ApiResult<Map<String, Object>> result = thirdApiMappingService.dispatch(map, "getGroupList");
        return result;
    }

    @Override
    public ApiResult createGroupServiceDay() {
        return null;
    }

    @Override
    public ApiResult getLatestMarketingGroupList() {
        return null;
    }

    @Override
    public ApiResult getFinishedCount(Integer type) {

        GroupServiceDayFinishedVO result = new GroupServiceDayFinishedVO();

        //默认查询本周
        Date startTime = DateUtils.getThisWeekMonday();
        String mobile = SmartGridContext.getMobile();
        if (type == 2) {
            startTime = DateUtils.getThisMonthFirstDay();
        }
        //查询已结束活动:实际结束时间 >= startTime && 实际结束时间 <= endTime
        GroupServiceDayQuery serviceDayQuery = new GroupServiceDayQuery();
        serviceDayQuery.setMobile(mobile);
        serviceDayQuery.setEndFilterStartTIme(startTime);
        serviceDayQuery.setEndFilterEndTIme(new Date());
        List<GroupServiceDayDO> groupServiceDayDOS = groupServiceDayMapper.find(serviceDayQuery);
        if (CollectionUtils.isEmpty(groupServiceDayDOS)) {
            log.error("[getFinishedCount] activityList is empty!");
            result.setActivityCount(0);
            result.setBusinessCount(0);
            return ApiResult.of(0,result);
        }
        result.setActivityCount(groupServiceDayDOS.size());
        List<Long> activityIdList = groupServiceDayDOS.stream().map(GroupServiceDayDO::getId).collect(Collectors.toList());
        GroupServiceDayMarketingNumberQuery numberQuery = new GroupServiceDayMarketingNumberQuery();
        numberQuery.setGroupServiceDayIds(activityIdList);
        List<GroupServiceDayMarketingNumberDO> numberDOS = groupServiceDayMarketingNumberMapper.find(numberQuery);
        if (CollectionUtils.isEmpty(numberDOS)) {
            log.error("[getFinishedCount] market number list is empty!");
            result.setBusinessCount(0);
            return ApiResult.of(0,result);
        }
        Integer businessCount = numberDOS.stream().collect(Collectors.summingInt(GroupServiceDayMarketingNumberDO::getCount));
        result.setBusinessCount(businessCount);
        return ApiResult.of(0,result);
    }

    @Override
    public ApiResult getActivityListByStatus() {
        return null;
    }

    @Override
    public ApiResult startSign(GroupServiceDaySignRequest request) {
        GroupServiceDayQuery groupServiceDayQuery = new GroupServiceDayQuery();
        groupServiceDayQuery.setId(request.getId());
        GroupServiceDayDO groupServiceDayDO = groupServiceDayMapper.get(groupServiceDayQuery);
        if (groupServiceDayDO == null) {
            ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

        return null;
    }

    @Override
    public ApiResult endSign(GroupServiceDaySignRequest request) {
        return null;
    }

    @Override
    public ApiResult cancel(Long id) {
        return null;
    }

    @Override
    public ApiResult getPartnerList(GroupServiceDayPartnerListRequest request) {
        return null;
    }
}
