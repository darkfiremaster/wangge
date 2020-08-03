package com.shinemo.wangge.core.service.groupserviceday.impl;

import cn.hutool.core.util.StrUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
