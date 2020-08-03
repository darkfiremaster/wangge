package com.shinemo.wangge.core.service.groupserviceday.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.model.ParentGroupServiceDayDO;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.core.util.ValidatorUtil;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.ParentGroupServiceDayMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    private ParentGroupServiceDayMapper parentGroupServiceDayMapper;

    @Resource
    private GroupServiceDayMapper groupServiceDayMapper;


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
    @Transactional
    public ApiResult<Void> createGroupServiceDay(GroupServiceDayRequest groupServiceDayRequest) {
        ValidatorUtil.validateEntity(groupServiceDayRequest);

        //生成父活动
        ParentGroupServiceDayDO parentGroupServiceDayDO = new ParentGroupServiceDayDO();
        parentGroupServiceDayDO.setTitle(groupServiceDayRequest.getTitle());
        parentGroupServiceDayDO.setGroupId(groupServiceDayRequest.getGroupId());
        parentGroupServiceDayDO.setGroupName(groupServiceDayRequest.getGroupName());
        parentGroupServiceDayDO.setGroupAddress(groupServiceDayRequest.getGroupAddress());
        parentGroupServiceDayDO.setCreatorId(Long.valueOf(SmartGridContext.getUid()));
        parentGroupServiceDayDO.setCreatorOrgId(Long.valueOf(SmartGridContext.getOrgId()));
        parentGroupServiceDayDO.setCreatorName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        parentGroupServiceDayDO.setMobile(SmartGridContext.getMobile());
        parentGroupServiceDayDO.setPlanStartTime(DateUtil.parseDateTime(groupServiceDayRequest.getPlanStartTime()));
        parentGroupServiceDayDO.setPlanEndTime(DateUtil.parseDateTime(groupServiceDayRequest.getPlanEndTime()));
        parentGroupServiceDayDO.setLocation(groupServiceDayRequest.getLocation());
        parentGroupServiceDayDO.setPartner(GsonUtils.toJson(groupServiceDayRequest.getPartner()));
        parentGroupServiceDayDO.setStatus(GroupServiceDayStatusEnum.NOT_START.getId());
        try {
            parentGroupServiceDayDO.setGridId(SmartGridContext.getSelectGridUserRoleDetail().getId());
        } catch (Exception e) {
            log.error("[createGroupServiceDay] 该用户无网格,mobile:{}", SmartGridContext.getMobile());
            parentGroupServiceDayDO.setGridId("");
        }
        parentGroupServiceDayMapper.insert(parentGroupServiceDayDO);

        List<GroupServiceDayRequest.PartnerBean> partnerList = groupServiceDayRequest.getPartner();
        for (GroupServiceDayRequest.PartnerBean partnerBean : partnerList) {
            //生成子活动
            GroupServiceDayDO groupServiceDayDO = new GroupServiceDayDO();
            groupServiceDayDO.setParentId(parentGroupServiceDayDO.getId());
            groupServiceDayDO.setTitle(parentGroupServiceDayDO.getTitle());
            groupServiceDayDO.setGroupId(parentGroupServiceDayDO.getGroupId());
            groupServiceDayDO.setGroupName(parentGroupServiceDayDO.getGroupName());
            groupServiceDayDO.setCreatorId(parentGroupServiceDayDO.getCreatorId());
            groupServiceDayDO.setCreatorName(parentGroupServiceDayDO.getCreatorName());
            groupServiceDayDO.setPlanStartTime(parentGroupServiceDayDO.getPlanStartTime());
            groupServiceDayDO.setPlanEntTime(parentGroupServiceDayDO.getPlanEndTime());
            groupServiceDayDO.setGroupAddress(parentGroupServiceDayDO.getGroupAddress());
            groupServiceDayDO.setLocation(parentGroupServiceDayDO.getLocation());
            groupServiceDayDO.setPartner(parentGroupServiceDayDO.getPartner());
            groupServiceDayDO.setStatus(GroupServiceDayStatusEnum.NOT_START.getId());
            groupServiceDayDO.setMobile(partnerBean.getMobile());
            groupServiceDayDO.setName(partnerBean.getName());
            groupServiceDayDO.setGridId(partnerBean.getGridId());
            groupServiceDayMapper.insert(groupServiceDayDO);

            //todo 同步华为
        }

        log.info("[createGroupServiceDay] 新建集团服务日成功, id:{},mobile:{}", parentGroupServiceDayDO.getId(), parentGroupServiceDayDO.getMobile());
        return ApiResult.of(0);
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
