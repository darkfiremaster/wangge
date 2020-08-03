package com.shinemo.wangge.core.service.groupserviceday.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author shangkaihui
 * @Date 2020/8/3 11:37
 * @Desc
 */
@Service
@Slf4j
public class GroupServiceDayServiceImpl implements GroupServiceDayService {


    @Override
    public ApiResult getGroupList() {
        return null;
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
        return null;
    }

    @Override
    public ApiResult getActivityListByStatus() {
        return null;
    }

    @Override
    public ApiResult startSign(GroupServiceDaySignRequest request) {
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
