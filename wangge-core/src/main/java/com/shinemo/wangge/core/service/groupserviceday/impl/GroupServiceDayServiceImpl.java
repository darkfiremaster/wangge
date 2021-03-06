package com.shinemo.wangge.core.service.groupserviceday.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.constant.GroupServiceDayConstants;
import com.shinemo.groupserviceday.domain.enums.HuaweiGroupServiceDayUrlEnum;
import com.shinemo.groupserviceday.domain.huawei.HuaWeiCreateGroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.model.*;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayMarketingNumberQuery;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.domain.query.ParentGroupServiceDayQuery;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceListRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayAreaInfoVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBizDetailVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayFinishedVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayVO;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.thirdapi.common.error.ThirdApiErrorCodes;
import com.shinemo.wangge.core.config.StallUpConfig;
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
    private ThirdApiMappingV2Service thirdApiMappingV2Service;
    @Resource
    private GroupServiceDayMapper groupServiceDayMapper;
    @Resource
    private GroupServiceDayMarketingNumberMapper groupServiceDayMarketingNumberMapper;
    @Resource
    private SignRecordMapper signRecordMapper;
    @Resource
    private ParentGroupServiceDayMapper parentGroupServiceDayMapper;
    @Resource
    private StallUpConfig stallUpConfig;


    @Override
    public ApiResult<Map<String, Object>> getGroupList(String groupName) {
        //透传华为
        HashMap<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(groupName)) {
            map.put("groupName", groupName);
        }
        //map.put("mobile", SmartGridContext.getMobile());
        ApiResult<Map<String, Object>> result = thirdApiMappingV2Service.dispatch(map, HuaweiGroupServiceDayUrlEnum.GET_GROUP_LIST.getApiName());
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
        List<GroupServiceDayDO> childActivityList = new ArrayList<>();
        for (GroupServiceDayRequest.PartnerBean partnerBean : partnerList) {
            //创建子活动
            GroupServiceDayDO groupServiceDayDO = getGroupServiceDayDO(parentGroupServiceDayDO, partnerBean);
            groupServiceDayMapper.insert(groupServiceDayDO);
            childActivityList.add(groupServiceDayDO);
        }

        //同步华为
        syncCreateGroupServiceDay(groupServiceDayRequest, parentGroupServiceDayDO, childActivityList);

        log.info("[createGroupServiceDay] 新建集团服务日成功, 父活动id:{},创建人mobile:{}", parentGroupServiceDayDO.getId(), parentGroupServiceDayDO.getMobile());
        return ApiResult.of(0);
    }

    private void syncCreateGroupServiceDay(GroupServiceDayRequest groupServiceDayRequest, ParentGroupServiceDayDO parentGroupServiceDayDO, List<GroupServiceDayDO> childActivityList) {
        HuaWeiCreateGroupServiceDayRequest huaWeiCreateGroupServiceDayRequest = new HuaWeiCreateGroupServiceDayRequest();
        huaWeiCreateGroupServiceDayRequest.setParentActivityId(GroupServiceDayConstants.ID_PREFIX + parentGroupServiceDayDO.getId());
        huaWeiCreateGroupServiceDayRequest.setTitle(groupServiceDayRequest.getTitle());
        huaWeiCreateGroupServiceDayRequest.setStartTime(DateUtil.formatDateTime(parentGroupServiceDayDO.getPlanStartTime()));
        huaWeiCreateGroupServiceDayRequest.setEndTime(DateUtil.formatDateTime(parentGroupServiceDayDO.getPlanEndTime()));
        huaWeiCreateGroupServiceDayRequest.setStatus(String.valueOf(parentGroupServiceDayDO.getStatus()));
        huaWeiCreateGroupServiceDayRequest.setGroupId(groupServiceDayRequest.getGroupId());

        List<HuaWeiCreateGroupServiceDayRequest.ChildGroupServiceDay> childGroupServiceDayList = new ArrayList<>();

        for (GroupServiceDayDO groupServiceDayDO : childActivityList) {
            HuaWeiCreateGroupServiceDayRequest.ChildGroupServiceDay childGroupServiceDay = new HuaWeiCreateGroupServiceDayRequest.ChildGroupServiceDay();
            childGroupServiceDay.setActivityId(GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getId());
            List<HuaWeiCreateGroupServiceDayRequest.ChildGroupServiceDay.Participant> participantList = new ArrayList<>();
            List<GroupServiceDayRequest.PartnerBean> partnerBeanList = GsonUtils.fromJsonToList(groupServiceDayDO.getPartner(), GroupServiceDayRequest.PartnerBean[].class);
            for (GroupServiceDayRequest.PartnerBean partnerBean : partnerBeanList) {
                HuaWeiCreateGroupServiceDayRequest.ChildGroupServiceDay.Participant participant = new HuaWeiCreateGroupServiceDayRequest.ChildGroupServiceDay.Participant();
                //判断用户来源
                if (StrUtil.isNotBlank(partnerBean.getUserId())) {
                    //来自网格
                    participant.setUserSource("1");
                    participant.setUserId(partnerBean.getUserId());
                    participant.setUserPhone(partnerBean.getMobile());
                } else {
                    //来自通讯录
                    participant.setUserSource("2");
                    participant.setUserName(partnerBean.getName());
                    participant.setUserPhone(partnerBean.getMobile());
                }

                //判断参与人类型
                if (partnerBean.getMobile().equals(groupServiceDayDO.getMobile())) {
                    //负责人
                    participant.setUserType("1");
                } else {
                    //参与人
                    participant.setUserType("2");
                }

                participantList.add(participant);
            }
            childGroupServiceDay.setParticipantList(participantList);
            childGroupServiceDayList.add(childGroupServiceDay);
        }

        huaWeiCreateGroupServiceDayRequest.setChildrenList(childGroupServiceDayList);
        Map<String, Object> map = BeanUtil.beanToMap(huaWeiCreateGroupServiceDayRequest, false, true);
        log.info("[syncCreateGroupServiceDay] 新建集团服务日同步华为,请求参数:{}", GsonUtils.toJson(map));
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.CREATE_GROUP_SERVICE_DAY.getApiName(), SmartGridContext.getMobile());
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
        serviceDayQuery.setEndFilterStartTime(startTime);
        serviceDayQuery.setEndFilterEndTime(new Date());
        List<Integer> statusList = new ArrayList<>();
        statusList.add(GroupServiceDayStatusEnum.END.getId());
        statusList.add(GroupServiceDayStatusEnum.ABNORMAL_END.getId());
        statusList.add(GroupServiceDayStatusEnum.AUTO_END.getId());
        serviceDayQuery.setStatusList(statusList);
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
        if (request.getStartTime() != null) {
            serviceDayQuery.setEndFilterStartTime(new Date(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            serviceDayQuery.setEndFilterEndTime(new Date(request.getEndTime()));
        }
        if (request.getStatus().equals(GroupServiceDayStatusEnum.END.getId())) {
            serviceDayQuery.setPageEnable(true);
            List<Integer> statusList = new ArrayList<>();
            statusList.add(GroupServiceDayStatusEnum.END.getId());
            statusList.add(GroupServiceDayStatusEnum.AUTO_END.getId());
            statusList.add(GroupServiceDayStatusEnum.ABNORMAL_END.getId());
            serviceDayQuery.setStatusList(statusList);
            serviceDayQuery.setStatus(null);
            serviceDayQuery.setOrderByEnable(true);
            serviceDayQuery.putOrderBy("real_end_time",false);
        }else {
            serviceDayQuery.setPageEnable(false);
        }

        if (request.getStatus().equals(GroupServiceDayStatusEnum.NOT_START.getId())) {
            serviceDayQuery.setOrderByEnable(true);
            serviceDayQuery.putOrderBy("plan_start_time",true);
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
            List<GroupServiceDayMarketingNumberDO> numberDOS = map.get(serviceDayVO.getId());
            if (CollectionUtils.isEmpty(numberDOS)) {
                serviceDayVO.setBusinessCount(0);
            }else {
                serviceDayVO.setBusinessCount(numberDOS.get(0).getCount());
            }
        }
        return ApiResult.of(0, ListVO.list(vos, count));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> startSign(GroupServiceDaySignRequest request) {

        GroupServiceDayDO groupServiceDayDO = getDOById(request.getActivityId());
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

//        if (!SmartGridContext.getMobile().equals(groupServiceDayDO.getMobile())) {
//            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.AUTH_ERROR);
//        }

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

        //同步华为
        startSignSyncHuaWei(request, groupServiceDayDO);

        return ApiResult.of(0);
    }

    private void startSignSyncHuaWei(GroupServiceDaySignRequest request, GroupServiceDayDO groupServiceDayDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getId());
        map.put("parentActivityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getParentId());
        map.put("status", GroupServiceDayStatusEnum.PROCESSING.getId());
        String location = request.getLocationDetailVO().getLocation();
        String[] locations = StrUtil.split(location, ",");
        map.put("startLongitude", locations[0]);
        map.put("startLatitude", locations[1]);
        map.put("startAddress", request.getLocationDetailVO().getAddress());
        map.put("startTime", DateUtil.formatDateTime(groupServiceDayDO.getRealStartTime()));
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.UPDATE_GROUP_SERVICE_DAY.getApiName(), SmartGridContext.getMobile());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> endSign(GroupServiceDaySignRequest request) {

        GroupServiceDayDO groupServiceDayDO = getDOById(request.getActivityId());
        if (groupServiceDayDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

//        if (!SmartGridContext.getMobile().equals(groupServiceDayDO.getMobile())) {
//            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.AUTH_ERROR);
//        }

        //校验活动状态
        if (!groupServiceDayDO.getStatus().equals(GroupServiceDayStatusEnum.PROCESSING.getId())) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_END_ERROR);
        }

        //距离校验
        String location = request.getLocationDetailVO().getLocation();
        ApiResult apiResult = checkDistaneWhencSign(groupServiceDayDO.getLocation(), location);

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
        updateActivityDO.setRealEndTime(endTime);
        if (apiResult == null) {
            updateActivityDO.setStatus(GroupServiceDayStatusEnum.END.getId());
        }else {
            updateActivityDO.setStatus(GroupServiceDayStatusEnum.ABNORMAL_END.getId());
        }
        groupServiceDayMapper.update(updateActivityDO);
        //更新父活动
        updateParentStatus(groupServiceDayDO, GroupServiceDayStatusEnum.END.getId(), endTime);

        //同步华为
        endSignSyncHuaWei(request, groupServiceDayDO);
        return ApiResult.of(0);
    }

    private void endSignSyncHuaWei(GroupServiceDaySignRequest request, GroupServiceDayDO groupServiceDayDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getId());
        map.put("parentActivityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getParentId());
        map.put("status", GroupServiceDayStatusEnum.END.getId());
        String[] split = StrUtil.split(request.getLocationDetailVO().getLocation(), ",");
        map.put("endLongitude", split[0]);
        map.put("endLatitude", split[1]);
        map.put("endAddress", request.getLocationDetailVO().getAddress());
        map.put("endTime", DateUtil.formatDateTime(groupServiceDayDO.getRealEndTime()));
        map.put("remark", request.getRemark());
        map.put("picUrl", CollUtil.join(request.getPicUrls(), ","));
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.UPDATE_GROUP_SERVICE_DAY.getApiName(), SmartGridContext.getMobile());
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

        //同步华为
        cancelSyncHuaWei(groupServiceDayDO);
        return ApiResult.of(0);
    }

    private void cancelSyncHuaWei(GroupServiceDayDO groupServiceDayDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getId());
        map.put("parentActivityId", GroupServiceDayConstants.ID_PREFIX + groupServiceDayDO.getParentId());
        map.put("status", GroupServiceDayStatusEnum.CANCEL.getId());
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.UPDATE_GROUP_SERVICE_DAY.getApiName(), SmartGridContext.getMobile());
    }

    @Override
    public ApiResult<Map<String, Object>> getPartnerList(Map<String, Object> requestData) {
        String param = (String)requestData.get("queryParam");
        requestData.remove("queryParam");
        requestData.put("userName",param);
        requestData.put("userPhone",param);
        if (requestData.get("pageNum") == null) {
            requestData.put("pageNum","1");
        }
        if (requestData.get("pageSize") == null) {
            requestData.put("pageSize","20");
        }
        return thirdApiMappingV2Service.dispatch(requestData, "getPartnerList");
    }

    @Override
    public ApiResult<List<GroupServiceDayAreaInfoVO>> getAreaInformation(Map<String, Object> requestData) {
        ApiResult<Map<String, Object>> apiResult = thirdApiMappingV2Service.dispatch(requestData, "getAreaInformation");
        if (apiResult != null && apiResult.isSuccess()) {
            Map<String, Object> data = apiResult.getData();
            if (!CollectionUtils.isEmpty(data)) {
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(GsonUtils.toJson(data.get("data"))).getAsJsonArray();
                List<GroupServiceDayAreaInfoVO> list = GsonUtils.jsonArrayToList(jsonArray, GroupServiceDayAreaInfoVO.class);
                return ApiResult.of(0,list);
            }else {
                return ApiResult.of(0,new ArrayList<>());
            }
        }
        return ApiResultWrapper.fail(ThirdApiErrorCodes.HUA_WEI_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
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
        serviceDayDO.setRealEndTime(endTime);
        groupServiceDayMapper.update(serviceDayDO);
        //更新父活动表
        updateParentStatus(serviceDayDO, status, endTime);

        //更新办理量
        GroupServiceDayMarketingNumberQuery numberQuery = new GroupServiceDayMarketingNumberQuery();
        numberQuery.setGroupServiceDayId(serviceDayDO.getId());
        GroupServiceDayMarketingNumberDO numberDO = groupServiceDayMarketingNumberMapper.get(numberQuery);
        if (numberDO == null) {
            numberDO = new GroupServiceDayMarketingNumberDO();
            numberDO.setCount(0);
            List<StallUpBizType> sweepFloorBizList = stallUpConfig.getConfig().getPublicGroupServiceDayBizDataList();
            List<GroupServiceDayBizDetailVO> details = new ArrayList<>();
            for (StallUpBizType bizType : sweepFloorBizList) {
                GroupServiceDayBizDetailVO bizDetail = new GroupServiceDayBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }
            GroupServiceDayMarketNumberDetail detail = GroupServiceDayMarketNumberDetail.builder()
                    .publicBizInfoList(details)
                    .build();
            numberDO.setDetail(GsonUtils.toJson(detail));
            numberDO.setGroupServiceDayId(serviceDayDO.getId());
            numberDO.setUserId(serviceDayDO.getCreatorId().toString());
            groupServiceDayMarketingNumberMapper.insert(numberDO);
        }

        //同步华为
        autoEndSyncHuaWei(serviceDayDO);

        return ApiResult.of(0);
    }

    private void autoEndSyncHuaWei(GroupServiceDayDO serviceDayDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + serviceDayDO.getId());
        map.put("parentActivityId", GroupServiceDayConstants.ID_PREFIX + serviceDayDO.getParentId());
        map.put("status", GroupServiceDayStatusEnum.AUTO_END.getId());
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.UPDATE_GROUP_SERVICE_DAY.getApiName(), serviceDayDO.getMobile());
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
            //判断父活动是否已开始
            ParentGroupServiceDayDO parentGroupServiceDay = getParentGroupServiceDayById(groupServiceDayDO.getParentId());
            if (parentGroupServiceDay.getStatus().equals(GroupServiceDayStatusEnum.NOT_START.getId())) {
                parentGroupServiceDayDO.setId(groupServiceDayDO.getParentId());
                parentGroupServiceDayDO.setStatus(GroupServiceDayStatusEnum.PROCESSING.getId());
                parentGroupServiceDayDO.setRealStartTime(time);
            }
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

    private ParentGroupServiceDayDO getParentGroupServiceDayById(Long id) {
        ParentGroupServiceDayQuery parentGroupServiceDayQuery = new ParentGroupServiceDayQuery();
        parentGroupServiceDayQuery.setId(id);
        ParentGroupServiceDayDO parentActivity = parentGroupServiceDayMapper.get(parentGroupServiceDayQuery);
        return parentActivity;
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
        if (distance > 5000) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.GROUP_SERVICE_SIGN_DISTANCE_ERROR);
        }
        return null;
    }


    private GroupServiceDayDO getGroupServiceDayDO(ParentGroupServiceDayDO parentGroupServiceDayDO, GroupServiceDayRequest.PartnerBean partnerBean) {
        GroupServiceDayDO groupServiceDayDO = new GroupServiceDayDO();
        groupServiceDayDO.setParentId(parentGroupServiceDayDO.getId());
        groupServiceDayDO.setTitle(parentGroupServiceDayDO.getTitle());
        groupServiceDayDO.setGroupId(parentGroupServiceDayDO.getGroupId());
        groupServiceDayDO.setGroupName(parentGroupServiceDayDO.getGroupName());
        groupServiceDayDO.setGroupAddress(parentGroupServiceDayDO.getGroupAddress());
        groupServiceDayDO.setGroupDetail(parentGroupServiceDayDO.getGroupDetail());
        groupServiceDayDO.setCreatorId(parentGroupServiceDayDO.getCreatorId());
        groupServiceDayDO.setCreatorName(parentGroupServiceDayDO.getCreatorName());
        groupServiceDayDO.setPlanStartTime(parentGroupServiceDayDO.getPlanStartTime());
        groupServiceDayDO.setPlanEndTime(parentGroupServiceDayDO.getPlanEndTime());
        groupServiceDayDO.setGroupAddress(parentGroupServiceDayDO.getGroupAddress());
        groupServiceDayDO.setLocation(parentGroupServiceDayDO.getLocation());
        groupServiceDayDO.setPartner(GsonUtils.toJson(parentGroupServiceDayDO.getPartner()));
        groupServiceDayDO.setStatus(GroupServiceDayStatusEnum.NOT_START.getId());
        groupServiceDayDO.setMobile(partnerBean.getMobile());
        groupServiceDayDO.setName(partnerBean.getName());
        //设置当前参与人的详情
        groupServiceDayDO.setExtend(GsonUtils.toJson(partnerBean));
        return groupServiceDayDO;
    }

    private ParentGroupServiceDayDO getParentGroupServiceDayDO(GroupServiceDayRequest groupServiceDayRequest) {
        ParentGroupServiceDayDO parentGroupServiceDayDO = new ParentGroupServiceDayDO();
        parentGroupServiceDayDO.setTitle(groupServiceDayRequest.getTitle());
        parentGroupServiceDayDO.setGroupId(groupServiceDayRequest.getGroupId());
        parentGroupServiceDayDO.setGroupName(groupServiceDayRequest.getGroupName());
        parentGroupServiceDayDO.setGroupAddress(groupServiceDayRequest.getGroupAddress());
        if (StrUtil.isNotBlank(SmartGridContext.getUid())) {
            parentGroupServiceDayDO.setCreatorId(Long.valueOf(SmartGridContext.getUid()));
        }
        if (StrUtil.isNotBlank(SmartGridContext.getOrgId())) {
            parentGroupServiceDayDO.setCreatorOrgId(Long.valueOf(SmartGridContext.getOrgId()));
        }
        parentGroupServiceDayDO.setCreatorName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        parentGroupServiceDayDO.setMobile(SmartGridContext.getMobile());
        LinkedHashMap<String, String> groupDetailMap = new LinkedHashMap<>();
        groupDetailMap.put("cityId", groupServiceDayRequest.getGroupCityId());
        groupDetailMap.put("cityName", groupServiceDayRequest.getGroupCityName());
        groupDetailMap.put("countryId", groupServiceDayRequest.getGroupCountryId());
        groupDetailMap.put("countryName", groupServiceDayRequest.getGroupCountryName());
        groupDetailMap.put("gridId", groupServiceDayRequest.getGroupGridId());
        groupDetailMap.put("gridName", groupServiceDayRequest.getGroupGridName());
        parentGroupServiceDayDO.setGroupDetail(GsonUtils.toJson(groupDetailMap));
        parentGroupServiceDayDO.setPlanStartTime(new Date(groupServiceDayRequest.getPlanStartTime()));
        parentGroupServiceDayDO.setPlanEndTime(new Date(groupServiceDayRequest.getPlanEndTime()));
        parentGroupServiceDayDO.setLocation(groupServiceDayRequest.getLocation());
        parentGroupServiceDayDO.setPartner(GsonUtils.toJson(groupServiceDayRequest.getPartner()));
        parentGroupServiceDayDO.setStatus(GroupServiceDayStatusEnum.NOT_START.getId());
        try {
            parentGroupServiceDayDO.setGridId(SmartGridContext.getSelectGridUserRoleDetail().getId());
        } catch (Exception e) {
            log.error("[createGroupServiceDay] 该用户无网格,mobile:{}", SmartGridContext.getMobile());
            parentGroupServiceDayDO.setGridId("0");
        }
        return parentGroupServiceDayDO;
    }

}
