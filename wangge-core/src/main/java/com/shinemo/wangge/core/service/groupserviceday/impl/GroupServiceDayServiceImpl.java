package com.shinemo.wangge.core.service.groupserviceday.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.constant.GroupServiceDayConstants;
import com.shinemo.groupserviceday.domain.enums.HuaweiGroupServiceDayUrlEnum;
import com.shinemo.groupserviceday.domain.model.GroupDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayMarketingNumberDO;
import com.shinemo.groupserviceday.domain.model.ParentGroupServiceDayDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayMarketingNumberQuery;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceListRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayFinishedVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayVO;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.core.util.ValidatorUtil;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMarketingNumberMapper;
import com.shinemo.wangge.dal.mapper.ParentGroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.SignRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private ThirdApiMappingV2Service thirdApiMappingService; //todo 等华为确定接口返回格式
    @Resource
    private GroupServiceDayMapper groupServiceDayMapper;
    @Resource
    private GroupServiceDayMarketingNumberMapper groupServiceDayMarketingNumberMapper;
    @Resource
    private SignRecordMapper signRecordMapper;
    @Resource
    private ParentGroupServiceDayMapper parentGroupServiceDayMapper;


    @Override
    public ApiResult<Map<String, Object>> getGroupList(String groupName) {
        //透传华为
        HashMap<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(groupName)) {
            map.put("groupName", groupName);
        }
        map.put("mobile", SmartGridContext.getMobile());
        ApiResult<Map<String, Object>> result = thirdApiMappingService.dispatch(map, HuaweiGroupServiceDayUrlEnum.GET_GROUP_LIST.getApiName());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> createGroupServiceDay(GroupServiceDayRequest groupServiceDayRequest) {
        ValidatorUtil.validateEntity(groupServiceDayRequest);

        //创建父活动
        ParentGroupServiceDayDO parentGroupServiceDayDO = getParentGroupServiceDayDO(groupServiceDayRequest);
        parentGroupServiceDayMapper.insert(parentGroupServiceDayDO);

        List<GroupServiceDayRequest.PartnerBean> partnerList = groupServiceDayRequest.getPartner();
        for (GroupServiceDayRequest.PartnerBean partnerBean : partnerList) {
            //创建子活动
            GroupServiceDayDO groupServiceDayDO = getGroupServiceDayDO(parentGroupServiceDayDO, partnerBean);
            groupServiceDayMapper.insert(groupServiceDayDO);

            //同步华为
            createGroupServiceDaySyncHuaWei(parentGroupServiceDayDO, partnerList, groupServiceDayDO);
        }

        log.info("[createGroupServiceDay] 新建集团服务日成功, 父活动id:{},创建人mobile:{}", parentGroupServiceDayDO.getId(), parentGroupServiceDayDO.getMobile());
        return ApiResult.of(0);
    }




    @Override
    public ApiResult<List<GroupDO>> getLatestMarketingGroupList() {
        List<GroupDO> latestGroupList = groupServiceDayMapper.getLatestMarketingGroupList(SmartGridContext.getMobile());
        return ApiResult.of(0, latestGroupList);
    }

    @Override
    public ApiResult<GroupServiceDayFinishedVO> getFinishedCount(Integer type) {

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
            return ApiResult.of(0, result);
        }
        result.setActivityCount(groupServiceDayDOS.size());
        List<Long> activityIdList = groupServiceDayDOS.stream().map(GroupServiceDayDO::getId).collect(Collectors.toList());
        GroupServiceDayMarketingNumberQuery numberQuery = new GroupServiceDayMarketingNumberQuery();
        numberQuery.setGroupServiceDayIds(activityIdList);
        List<GroupServiceDayMarketingNumberDO> numberDOS = groupServiceDayMarketingNumberMapper.find(numberQuery);
        if (CollectionUtils.isEmpty(numberDOS)) {
            log.error("[getFinishedCount] market number list is empty!");
            result.setBusinessCount(0);
            return ApiResult.of(0, result);
        }
        Integer businessCount = numberDOS.stream().collect(Collectors.summingInt(GroupServiceDayMarketingNumberDO::getCount));
        result.setBusinessCount(businessCount);
        return ApiResult.of(0, result);
    }

    @Override
    public ApiResult<ListVO<GroupServiceDayVO>> getActivityListByStatus(GroupServiceListRequest request) {
        GroupServiceDayQuery serviceDayQuery = new GroupServiceDayQuery();
        serviceDayQuery.setStatus(request.getStatus());
        serviceDayQuery.setMobile(SmartGridContext.getMobile());
        serviceDayQuery.setEndFilterStartTIme(request.getStartTime());
        serviceDayQuery.setEndFilterEndTIme(request.getEndTime());
        if (request.getStatus().equals(GroupServiceDayStatusEnum.END.getId())) {
            serviceDayQuery.setPageEnable(true);
        }
        List<GroupServiceDayDO> groupServiceDayDOS = groupServiceDayMapper.find(serviceDayQuery);
        if (CollectionUtils.isEmpty(groupServiceDayDOS)) {
            return ApiResult.of(0, ListVO.list(new ArrayList<>(), 0));
        }
        //DO-->VO
        List<GroupServiceDayVO> vos = new ArrayList<>(groupServiceDayDOS.size());
        for (GroupServiceDayDO serviceDayDO : groupServiceDayDOS) {
            GroupServiceDayVO serviceDayVO = new GroupServiceDayVO();
            BeanUtils.copyProperties(serviceDayDO, serviceDayVO);
            vos.add(serviceDayVO);
        }
        if (!request.getStatus().equals(GroupServiceDayStatusEnum.END.getId())) {
            return ApiResult.of(0, ListVO.list(vos, vos.size()));
        }
        long count = groupServiceDayMapper.count(serviceDayQuery);
        //查询办理量
        List<Long> activityIds = vos.stream().map(GroupServiceDayVO::getId).collect(Collectors.toList());
        GroupServiceDayMarketingNumberQuery numberQuery = new GroupServiceDayMarketingNumberQuery();
        numberQuery.setGroupServiceDayIds(activityIds);
        List<GroupServiceDayMarketingNumberDO> groupServiceDayMarketingNumberDOS = groupServiceDayMarketingNumberMapper.find(numberQuery);
        Map<Long, List<GroupServiceDayMarketingNumberDO>> map = groupServiceDayMarketingNumberDOS.stream().collect(Collectors.groupingBy(GroupServiceDayMarketingNumberDO::getGroupServiceDayId));
        for (GroupServiceDayVO serviceDayVO : vos) {
            GroupServiceDayMarketingNumberDO numberDO = map.get(serviceDayVO.getId()).get(0);
            serviceDayVO.setBusinessCount(numberDO.getCount());
        }
        return ApiResult.of(0, ListVO.list(vos, count));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> startSign(GroupServiceDaySignRequest request) {

        GroupServiceDayDO groupServiceDayDO = getDOById(request.getId());
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

        if (!SmartGridContext.getMobile().equals(groupServiceDayDO.getMobile())) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.AUTH_ERROR);
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
        updateParentStatus(groupServiceDayDO, GroupServiceDayStatusEnum.PROCESSING.getId(), startTime);

        //todo 同步华为

        return ApiResult.of(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> endSign(GroupServiceDaySignRequest request) {

        GroupServiceDayDO groupServiceDayDO = getDOById(request.getId());
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

        if (!SmartGridContext.getMobile().equals(groupServiceDayDO.getMobile())) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.AUTH_ERROR);
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
            log.error("[endSign] signRecordDO is null,activityId = {}", groupServiceDayDO.getId());
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
        updateParentStatus(groupServiceDayDO, GroupServiceDayStatusEnum.END.getId(), endTime);

        //todo 同步华为
        return ApiResult.of(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> cancel(Long id) {

        GroupServiceDayDO groupServiceDayDO = getDOById(id);
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }
        if (!SmartGridContext.getMobile().equals(groupServiceDayDO.getMobile())) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.AUTH_ERROR);
        }
        //更新子活动
        GroupServiceDayDO updateDO = new GroupServiceDayDO();
        updateDO.setId(groupServiceDayDO.getId());
        updateDO.setStatus(GroupServiceDayStatusEnum.CANCEL.getId());
        groupServiceDayMapper.update(updateDO);
        //更新父活动
        updateParentStatus(groupServiceDayDO, GroupServiceDayStatusEnum.CANCEL.getId(), new Date());

        //todo 同步华为
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<Map<String, Object>> getPartnerList(Map<String, Object> requestData) {
        return thirdApiMappingService.dispatch(requestData, "getPartnerList");
    }

    @Override
    public ApiResult<Void> autoEnd(GroupServiceDayDO serviceDayDO) {
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setActivityId(serviceDayDO.getId());
        signRecordQuery.setBizType(SignRecordBizTypeEnum.GROUP_SERVICE_DAY.getId());
        SignRecordDO signRecordDO = signRecordMapper.get(signRecordQuery);

        int status = GroupServiceDayStatusEnum.AUTO_END.getId();
        Date endTime = new Date();

        //更新签到表
        if (signRecordDO == null) {
            signRecordDO = new SignRecordDO();
            signRecordDO.setEndTime(endTime);
            signRecordDO.setBizType(SignRecordBizTypeEnum.GROUP_SERVICE_DAY.getId());
            signRecordDO.setUserId(serviceDayDO.getCreatorId().toString());
            signRecordDO.setActivityId(serviceDayDO.getId());
            signRecordDO.setMobile(serviceDayDO.getMobile());
            signRecordMapper.insert(signRecordDO);
        } else {
            SignRecordDO newSignRecordDO = new SignRecordDO();
            newSignRecordDO.setId(signRecordDO.getId());
            newSignRecordDO.setEndTime(endTime);
            signRecordMapper.update(newSignRecordDO);
        }

        //更新子活动表
        serviceDayDO.setStatus(status);
        groupServiceDayMapper.update(serviceDayDO);
        //更新父活动表
        updateParentStatus(serviceDayDO, status, endTime);

        return ApiResult.of(0);
    }

    /**
     * 更新父活动状态
     *
     * @param groupServiceDayDO
     * @param status
     */
    private void updateParentStatus(GroupServiceDayDO groupServiceDayDO, Integer status, Date time) {

        ParentGroupServiceDayDO parentGroupServiceDayDO = new ParentGroupServiceDayDO();
        //子活动有一个开始，父活动即为开始
        if (status == GroupServiceDayStatusEnum.PROCESSING.getId()) {
            parentGroupServiceDayDO.setId(groupServiceDayDO.getParentId());
            parentGroupServiceDayDO.setStatus(GroupServiceDayStatusEnum.PROCESSING.getId());
            parentGroupServiceDayDO.setRealStartTime(time);
        } else if (status == GroupServiceDayStatusEnum.END.getId() ||
                status == GroupServiceDayStatusEnum.ABNORMAL_END.getId()
                || status == GroupServiceDayStatusEnum.CANCEL.getId()
                || status == GroupServiceDayStatusEnum.AUTO_END.getId()) {
            //所有子活动结束，父活动即为结束
            GroupServiceDayQuery serviceDayQuery = new GroupServiceDayQuery();
            serviceDayQuery.setParentId(groupServiceDayDO.getParentId());
            List<GroupServiceDayDO> groupServiceDayDOS = groupServiceDayMapper.find(serviceDayQuery);
            List<GroupServiceDayDO> notEndList = groupServiceDayDOS.stream().
                    filter(a -> a.getStatus().equals(GroupServiceDayStatusEnum.NOT_START.getId()) ||
                            a.getStatus().equals(GroupServiceDayStatusEnum.PROCESSING.getId())).
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
     *
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
     *
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

    private void createGroupServiceDaySyncHuaWei(ParentGroupServiceDayDO parentGroupServiceDayDO, List<GroupServiceDayRequest.PartnerBean> partnerList, GroupServiceDayDO groupServiceDayDO) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getId());
        map.put("parentActivityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getParentId());
        map.put("creatorMobile", parentGroupServiceDayDO.getMobile());
        map.put("creatorGridId", parentGroupServiceDayDO.getGridId());
        map.put("creatorName", parentGroupServiceDayDO.getCreatorName());
        map.put("participantMobile", groupServiceDayDO.getMobile());
        map.put("participantGridId", groupServiceDayDO.getGridId());
        map.put("participantName", groupServiceDayDO.getName());
        map.put("title", groupServiceDayDO.getTitle());
        map.put("startTime", String.valueOf(parentGroupServiceDayDO.getPlanStartTime().getTime()));
        map.put("entTime", String.valueOf(parentGroupServiceDayDO.getPlanEndTime().getTime()));
        map.put("status", groupServiceDayDO.getStatus());
        map.put("groupId", groupServiceDayDO.getGroupId());
        map.put("groupName", groupServiceDayDO.getGroupName());
        map.put("groupAddress", groupServiceDayDO.getGroupAddress());
        map.put("createTime", String.valueOf(System.currentTimeMillis()));
        map.put("updateTime", String.valueOf(System.currentTimeMillis()));
        map.put("participantList", partnerList);
        thirdApiMappingService.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.CREATE_GROUP_SERVICE_DAY.getApiName(), SmartGridContext.getMobile());
    }

    private GroupServiceDayDO getGroupServiceDayDO(ParentGroupServiceDayDO parentGroupServiceDayDO, GroupServiceDayRequest.PartnerBean partnerBean) {
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
        return groupServiceDayDO;
    }

    private ParentGroupServiceDayDO getParentGroupServiceDayDO(GroupServiceDayRequest groupServiceDayRequest) {
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
        return parentGroupServiceDayDO;
    }

}
