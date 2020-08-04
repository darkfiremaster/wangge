package com.shinemo.wangge.core.service.groupserviceday.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayMarketingNumberDO;
import com.shinemo.groupserviceday.domain.model.ParentGroupServiceDayDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayMarketingNumberQuery;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayFinishedVO;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.common.error.SweepFloorErrorCodes;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.core.util.ValidatorUtil;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMarketingNumberMapper;
import com.shinemo.wangge.dal.mapper.ParentGroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Resource
    private SignRecordMapper signRecordMapper;
    @Resource
    private ParentGroupServiceDayMapper parentGroupServiceDayMapper;

    @Value("${sign.distance.switch}")
    public boolean signSwitch;



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
    public ApiResult<List<GroupDO>> getLatestMarketingGroupList() {
        List<GroupDO> latestGroupList = groupServiceDayMapper.getLatestMarketingGroupList(SmartGridContext.getMobile());
        return ApiResult.of(0,latestGroupList);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult startSign(GroupServiceDaySignRequest request) {

        GroupServiceDayDO groupServiceDayDO = getDOById(request.getId());
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

        //校验当前活动状态
        if (GroupServiceDayStatusEnum.NOT_START.getId() != groupServiceDayDO.getStatus()) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_START_ERROR);
        }

        //校验是否存在进行中活动
        ApiResult checkResult = checkStatusWhenStart();
        if (checkResult != null) {
            return checkResult;
        }

        //插入签到表
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setActivityId(groupServiceDayDO.getId());
        signRecordQuery.setBizType(SignRecordBizTypeEnum.GROUP_SERVICE_DAY.getId());
        SignRecordDO signRecordDO = new SignRecordDO();
        signRecordDO.setUserName(SmartGridContext.getUserName());
        signRecordDO.setMobile(SmartGridContext.getMobile());
        signRecordDO.setActivityId(groupServiceDayDO.getId());
        signRecordDO.setUserId(SmartGridContext.getUid());
        signRecordDO.setBizType(SignRecordBizTypeEnum.GROUP_SERVICE_DAY.getId());
        signRecordDO.setStartLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        signRecordDO.setStartTime(new Date());
        signRecordMapper.insert(signRecordDO);
        //更新子活动状态
        Date startTime = new Date();
        GroupServiceDayDO updateActivityDO = new GroupServiceDayDO();
        updateActivityDO.setId(groupServiceDayDO.getId());
        updateActivityDO.setStatus(GroupServiceDayStatusEnum.PROCESSING.getId());
        updateActivityDO.setRealStartTime(startTime);
        groupServiceDayMapper.update(updateActivityDO);
        //更新父活动状态
        updateParentStatus(groupServiceDayDO,GroupServiceDayStatusEnum.PROCESSING.getId(),startTime);

        return ApiResult.of(0);
    }

    @Override
    public ApiResult endSign(GroupServiceDaySignRequest request) {

        GroupServiceDayDO groupServiceDayDO = getDOById(request.getId());
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

        //校验活动状态
        if (!groupServiceDayDO.getStatus().equals(GroupServiceDayStatusEnum.PROCESSING.getId())) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_END_ERROR);
        }

        //距离校验
        String location = request.getLocationDetailVO().getLocation();
        ApiResult apiResult = checkDistaneWhencSign(groupServiceDayDO.getLocation(), location);
        if (!apiResult.isSuccess()) {
            return apiResult;
        }
        //更新签到表
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setActivityId(groupServiceDayDO.getId());
        SignRecordDO signRecordDO = signRecordMapper.get(signRecordQuery);
        if (signRecordDO == null) {
            log.error("[endSign] signRecordDO is null,activityId = {}",groupServiceDayDO.getId());
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.BASE_ERROR);
        }
        SignRecordDO updateSignDO = new SignRecordDO();
        updateSignDO.setId(signRecordDO.getId());
        updateSignDO.setRemark(request.getRemark());
        updateSignDO.setEndTime(new Date());
        updateSignDO.setEndLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        updateSignDO.setImgUrl(GsonUtils.toJson(request.getPicUrls()));
        signRecordMapper.update(updateSignDO);
        //更新子活动表
        Date endTime = new Date();
        GroupServiceDayDO updateActivityDO = new GroupServiceDayDO();
        updateActivityDO.setId(groupServiceDayDO.getId());
        updateActivityDO.setStatus(GroupServiceDayStatusEnum.END.getId());
        updateActivityDO.setRealEndTime(endTime);
        groupServiceDayMapper.update(updateActivityDO);
        //更新父活动
        updateParentStatus(groupServiceDayDO,GroupServiceDayStatusEnum.END.getId(),endTime);

        return ApiResult.of(0);
    }

    @Override
    public ApiResult cancel(Long id) {
        return null;
    }

    @Override
    public ApiResult getPartnerList(GroupServiceDayPartnerListRequest request) {
        return null;
    }

    /**
     * 更新父活动状态
     * @param groupServiceDayDO
     * @param status
     */
    private void updateParentStatus(GroupServiceDayDO groupServiceDayDO,Integer status,Date time) {

        ParentGroupServiceDayDO parentGroupServiceDayDO = new ParentGroupServiceDayDO();
        //子活动有一个开始，父活动即为开始
        if (status == GroupServiceDayStatusEnum.PROCESSING.getId()) {
            parentGroupServiceDayDO.setId(groupServiceDayDO.getParentId());
            parentGroupServiceDayDO.setStatus(GroupServiceDayStatusEnum.PROCESSING.getId());
            parentGroupServiceDayDO.setRealStartTime(time);
        }else if (status == GroupServiceDayStatusEnum.END.getId()) {
            //所有子活动结束，父活动即为结束
            GroupServiceDayQuery serviceDayQuery = new GroupServiceDayQuery();
            serviceDayQuery.setParentId(groupServiceDayDO.getParentId());
            List<GroupServiceDayDO> groupServiceDayDOS = groupServiceDayMapper.find(serviceDayQuery);
            List<GroupServiceDayDO> notEndList = groupServiceDayDOS.stream().
                    filter(a -> !a.getStatus().equals(GroupServiceDayStatusEnum.END.getId())).
                    collect(Collectors.toList());
            if (CollectionUtils.isEmpty(notEndList)) {
                //更新父活动状态未已结束
                parentGroupServiceDayDO.setStatus(GroupServiceDayStatusEnum.END.getId());
                parentGroupServiceDayDO.setId(groupServiceDayDO.getParentId());
                parentGroupServiceDayDO.setRealEndTime(time);
            }
        }
        parentGroupServiceDayMapper.update(parentGroupServiceDayDO);
    }

    private GroupServiceDayDO getDOById(Long id) {
        GroupServiceDayQuery groupServiceDayQuery = new GroupServiceDayQuery();
        groupServiceDayQuery.setId(id);
        return groupServiceDayMapper.get(groupServiceDayQuery);
    }

    /**
     * 校验是否有进行中活动
     * @return
     */
    private ApiResult checkStatusWhenStart() {

        GroupServiceDayQuery serviceDayQuery = new GroupServiceDayQuery();
        serviceDayQuery.setMobile(SmartGridContext.getMobile());
        serviceDayQuery.setStatus(GroupServiceDayStatusEnum.PROCESSING.getId());
        List<GroupServiceDayDO> groupServiceDayDOS = groupServiceDayMapper.find(serviceDayQuery);
        if (!CollectionUtils.isEmpty(groupServiceDayDOS)) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.STARTED_ACTIVITY_EXIT);
        }
        return null;
    }

    /**
     * 距离校验
     * @param dbLocation
     * @param reqLocation
     * @return
     */
    private ApiResult checkDistaneWhencSign(String dbLocation, String reqLocation) {
        String[] dbSplit = dbLocation.split(",");
        String[] reqSplit = reqLocation.split(",");
        double Lat1 = Double.parseDouble(dbSplit[1]);
        double Lon1 = Double.parseDouble(dbSplit[0]);
        double Lat2 = Double.parseDouble(reqSplit[1]);
        double Lon2 = Double.parseDouble(reqSplit[0]);
        double distance = DistanceUtils.getDistanceFromCoordinates(Lat1, Lon1, Lat2, Lon2);
        if (distance > 5) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.GROUP_SERVICE_SIGN_DISTANCE_ERROR);
        }
        return ApiResult.of(0);
    }

}
