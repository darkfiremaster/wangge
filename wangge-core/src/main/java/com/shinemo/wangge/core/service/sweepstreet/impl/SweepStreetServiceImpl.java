package com.shinemo.wangge.core.service.sweepstreet.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.constant.GroupServiceDayConstants;
import com.shinemo.groupserviceday.domain.enums.HuaweiGroupServiceDayUrlEnum;
import com.shinemo.groupserviceday.domain.huawei.HuaWeiCreateGroupServiceDayRequest;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.model.ParentGroupServiceDayDO;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayRequest;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import com.shinemo.sweepstreet.domain.contants.SweepStreetActivityConstants;
import com.shinemo.sweepstreet.domain.huawei.HuaWeiCreateSweepStreetActivityRequest;
import com.shinemo.sweepstreet.domain.model.ParentSweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetMarketingNumberDO;
import com.shinemo.sweepstreet.domain.query.ParentSweepStreetActivityQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetActivityQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetMarketingNumberQuery;
import com.shinemo.sweepstreet.domain.request.SweepStreetActivityRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetListRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetSignRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityFinishedVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import com.shinemo.sweepstreet.enums.HuaweiSweepStreetActivityUrlEnum;
import com.shinemo.sweepstreet.enums.SweepStreetStatusEnum;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.core.util.ValidatorUtil;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SweepStreetServiceImpl implements SweepStreetService {

    @Resource
    private SweepStreetActivityMapper sweepStreetActivityMapper;

    @Resource
    private SweepStreetMarketingNumberMapper sweepStreetMarketingNumberMapper;

    @Resource
    private SignRecordMapper signRecordMapper;

    @Resource
    private ParentSweepStreetActivityMapper parentSweepStreetActivityMapper;

    @Resource
    private ThirdApiMappingV2Service thirdApiMappingV2Service;

    @Override
    public ApiResult<ListVO<SweepStreetActivityVO>> getSweepStreetList(SweepStreetListRequest request) {
        SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
        streetActivityQuery.setStatus(request.getStatus());
        streetActivityQuery.setMobile(SmartGridContext.getMobile());
        if (request.getStartTime() != null) {
            streetActivityQuery.setEndFilterStartTime(new Date(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            streetActivityQuery.setEndFilterEndTime(new Date(request.getEndTime()));
        }
        if (request.getStatus().equals(SweepStreetStatusEnum.END.getId())) {
            streetActivityQuery.setPageEnable(true);
            List<Integer> statusList = new ArrayList<>();
            statusList.add(SweepStreetStatusEnum.ABNORMAL_END.getId());
            statusList.add(SweepStreetStatusEnum.AUTO_END.getId());
            statusList.add(SweepStreetStatusEnum.END.getId());
            streetActivityQuery.setStatusList(statusList);
            streetActivityQuery.setStatus(null);
        }
        List<SweepStreetActivityDO> activityDOS = sweepStreetActivityMapper.find(streetActivityQuery);
        if (CollectionUtils.isEmpty(activityDOS)) {
            return ApiResult.of(0, ListVO.list(new ArrayList<>(), 0));
        }
        //DO-->VO
        List<SweepStreetActivityVO> vos = new ArrayList<>(activityDOS.size());
        for (SweepStreetActivityDO activityDO : activityDOS) {
            SweepStreetActivityVO activityVO = new SweepStreetActivityVO();
            BeanUtils.copyProperties(activityDO, activityVO);
            vos.add(activityVO);
        }
        if (!request.getStatus().equals(SweepStreetStatusEnum.END.getId())) {
            if (request.getStatus().equals(SweepStreetStatusEnum.PROCESSING.getId())) {
                //进行中的查询打卡地址
                buildLocation(vos);
            }
            return ApiResult.of(0, ListVO.list(vos, vos.size()));
        }
        long count = sweepStreetActivityMapper.count(streetActivityQuery);
        //查询办理量
        List<Long> activityIds = vos.stream().map(SweepStreetActivityVO::getId).collect(Collectors.toList());
        SweepStreetMarketingNumberQuery numberQuery = new SweepStreetMarketingNumberQuery();
        numberQuery.setActivityIds(activityIds);
        List<SweepStreetMarketingNumberDO> numberDOS = sweepStreetMarketingNumberMapper.find(numberQuery);
        Map<Long, List<SweepStreetMarketingNumberDO>> map = numberDOS.stream().collect(Collectors.groupingBy(SweepStreetMarketingNumberDO::getSweepStreetId));
        for (SweepStreetActivityVO activityVO : vos) {
            List<SweepStreetMarketingNumberDO> marketingNumberDOS = map.get(activityVO.getId());
            if (CollectionUtils.isEmpty(marketingNumberDOS)) {
                activityVO.setBusinessCount(0);
            }else {
                activityVO.setBusinessCount(marketingNumberDOS.get(0).getCount());
            }
        }
        return ApiResult.of(0, ListVO.list(vos, count));
    }


    @Override
    public ApiResult startSign(SweepStreetSignRequest request) {
        SweepStreetActivityDO sweepStreetActivityDO = getDOById(request.getActivityId());
        if (sweepStreetActivityDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

//        if (!SmartGridContext.getMobile().equals(groupServiceDayDO.getMobile())) {
//            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.AUTH_ERROR);
//        }

        //校验当前活动状态
        if (SweepStreetStatusEnum.NOT_START.getId() != sweepStreetActivityDO.getStatus()) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_START_ERROR);
        }

        //校验是否存在进行中活动
        ApiResult checkResult = checkStatusWhenStart();
        if (checkResult != null) {
            return checkResult;
        }

        //插入签到表
        SignRecordDO signRecordDO = new SignRecordDO();
        signRecordDO.setUserName(SmartGridContext.getUserName());
        signRecordDO.setMobile(SmartGridContext.getMobile());
        signRecordDO.setActivityId(sweepStreetActivityDO.getId());
        signRecordDO.setUserId(SmartGridContext.getUid());
        signRecordDO.setBizType(SignRecordBizTypeEnum.SWEEP_STREET.getId());
        signRecordDO.setStartLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        signRecordDO.setStartTime(new Date());
        signRecordMapper.insert(signRecordDO);
        //更新子活动状态
        Date startTime = new Date();
        SweepStreetActivityDO updateActivityDO = new SweepStreetActivityDO();
        updateActivityDO.setId(sweepStreetActivityDO.getId());
        updateActivityDO.setStatus(SweepStreetStatusEnum.PROCESSING.getId());
        updateActivityDO.setRealStartTime(startTime);
        sweepStreetActivityMapper.update(updateActivityDO);
        //更新父活动状态
        updateParentStatus(sweepStreetActivityDO, SweepStreetStatusEnum.PROCESSING.getId(), startTime);

        //todo 同步华为


        return ApiResult.of(0);
    }

    @Override
    public ApiResult endSign(SweepStreetSignRequest request) {
        SweepStreetActivityDO sweepStreetActivityDO = getDOById(request.getActivityId());
        if (sweepStreetActivityDO == null) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_NOT_EXIT);
        }

        //校验当前活动状态，非进行中状态不可签离
        if (SweepStreetStatusEnum.PROCESSING.getId() != sweepStreetActivityDO.getStatus()) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_END_ERROR);
        }
        /*更新子活动表的个人活动数据*/
        SignRecordQuery query =new SignRecordQuery();
        query.setActivityId(request.getActivityId());
        query.setUserId(SmartGridContext.getUid());
        SignRecordDO signRecordDO=signRecordMapper.get(query);

        if (signRecordDO == null) {
            log.error("[endSign] signRecordDO is null,activityId = {}", sweepStreetActivityDO.getId());
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.ACTIVITY_SEARCH_ERROR);
        }

        SignRecordDO updateSignDO = new SignRecordDO();
        updateSignDO.setId(signRecordDO.getId());
        updateSignDO.setRemark(request.getRemark());
        updateSignDO.setEndTime(new Date());
        updateSignDO.setEndLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        updateSignDO.setImgUrl(GsonUtils.toJson(request.getPicUrls()));
        signRecordMapper.update(updateSignDO);

        /*更新子表活动记录*/
        Date endTime = new Date();
        SweepStreetActivityDO updateStreetActivityDO=new SweepStreetActivityDO();
        updateStreetActivityDO.setId(sweepStreetActivityDO.getId());
        updateStreetActivityDO.setStatus(SweepStreetStatusEnum.END.getId());
        updateStreetActivityDO.setRealEndTime(endTime);
        sweepStreetActivityMapper.update(updateStreetActivityDO);
        //更新父活动
        updateParentStatus(sweepStreetActivityDO, SweepStreetStatusEnum.END.getId(), endTime);

        return ApiResult.of(0);
    }

    @Override
    public ApiResult<Void> autoEnd(SweepStreetActivityDO streetActivityDO) {
        SignRecordQuery query =new SignRecordQuery();
        query.setActivityId(streetActivityDO.getId());
        query.setUserId(SmartGridContext.getUid());
        SignRecordDO signRecordDO=signRecordMapper.get(query);

        int status = SweepStreetStatusEnum.AUTO_END.getId();
        Date endTime = new Date();

        //更新签到表
        if (signRecordDO == null) {
            signRecordDO = new SignRecordDO();
            signRecordDO.setEndTime(endTime);
            signRecordDO.setBizType(SignRecordBizTypeEnum.SWEEP_STREET.getId());
            signRecordDO.setUserId(streetActivityDO.getCreatorId().toString());
            signRecordDO.setActivityId(streetActivityDO.getId());
            signRecordDO.setMobile(streetActivityDO.getMobile());
            signRecordMapper.insert(signRecordDO);
        } else {
            SignRecordDO newSignRecordDO = new SignRecordDO();
            newSignRecordDO.setId(signRecordDO.getId());
            newSignRecordDO.setEndTime(endTime);
            signRecordMapper.update(newSignRecordDO);
        }

        //更新子活动表
        streetActivityDO.setStatus(status);
        sweepStreetActivityMapper.update(streetActivityDO);
        //更新父活动表
        updateParentStatus(streetActivityDO, status, endTime);
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<SweepStreetActivityFinishedVO> getFinishedCount(Integer type) {
        SweepStreetActivityFinishedVO result = new SweepStreetActivityFinishedVO();

        //默认查询本周
        Date startTime = DateUtils.getThisWeekMonday();
        String mobile = SmartGridContext.getMobile();
        if (type == 2) {
            startTime = DateUtils.getThisMonthFirstDay();
        }
        //查询已结束活动:实际结束时间 >= startTime && 实际结束时间 <= endTime
        SweepStreetActivityQuery query = new SweepStreetActivityQuery();
        query.setMobile(mobile);
        query.setEndFilterStartTime(startTime);
        query.setEndFilterEndTime(new Date());
        List<SweepStreetActivityDO> sweepStreetActivityDOS = sweepStreetActivityMapper.find(query);
        if (CollectionUtils.isEmpty(sweepStreetActivityDOS)) {
            log.error("[getFinishedCount] activityList is empty!");
            result.setActivityCount(0);
            result.setBusinessCount(0);
            return ApiResult.of(0, result);
        }
        result.setActivityCount(sweepStreetActivityDOS.size());
        List<Long> activityIdList = sweepStreetActivityDOS.stream().map(SweepStreetActivityDO::getId).collect(Collectors.toList());
        SweepStreetMarketingNumberQuery numberQuery = new SweepStreetMarketingNumberQuery();
        numberQuery.setActivityIds(activityIdList);
        List<SweepStreetMarketingNumberDO> numberDOS = sweepStreetMarketingNumberMapper.find(numberQuery);
        if (CollectionUtils.isEmpty(numberDOS)) {
            log.error("[getFinishedCount] market number list is empty!");
            result.setBusinessCount(0);
            return ApiResult.of(0, result);
        }
        Integer businessCount = numberDOS.stream().collect(Collectors.summingInt(SweepStreetMarketingNumberDO::getCount));
        result.setBusinessCount(businessCount);
        return ApiResult.of(0, result);
    }

    @Override
    public ApiResult<Void> createSweepStreet(SweepStreetActivityRequest sweepStreetActivityRequest) {
        ValidatorUtil.validateEntity(sweepStreetActivityRequest);

        //创建父活动
        ParentSweepStreetActivityDO parentSweepStreetActivityDO = getParentSweepStreetActivityDO(sweepStreetActivityRequest);
        parentSweepStreetActivityMapper.insert(parentSweepStreetActivityDO);

        List<SweepStreetActivityRequest.PartnerBean> partnerList = sweepStreetActivityRequest.getPartner();
        List<SweepStreetActivityDO> childActivityList = new ArrayList<>();
        for (SweepStreetActivityRequest.PartnerBean partnerBean : partnerList) {
            //创建子活动
            SweepStreetActivityDO sweepStreetActivityDO = getSweepStreetActivityDO(parentSweepStreetActivityDO, partnerBean);
            sweepStreetActivityMapper.insert(sweepStreetActivityDO);
            childActivityList.add(sweepStreetActivityDO);
        }

        //同步华为
        syncCreateSweepStreetActivity(sweepStreetActivityRequest, parentSweepStreetActivityDO, childActivityList);

        log.info("[createSweepStreet] 新建扫村活动成功, 父活动id:{},创建人mobile:{}", parentSweepStreetActivityDO.getId(), parentSweepStreetActivityDO.getMobile());
        return ApiResult.of(0);
    }

    private SweepStreetActivityDO getDOById(Long id) {
        SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
        streetActivityQuery.setId(id);
        return sweepStreetActivityMapper.get(streetActivityQuery);
    }

    /**
     * 校验是否有进行中活动
     *
     * @return
     */
    private ApiResult checkStatusWhenStart() {

        SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
        streetActivityQuery.setMobile(SmartGridContext.getMobile());
        streetActivityQuery.setStatus(SweepStreetStatusEnum.PROCESSING.getId());
        List<SweepStreetActivityDO> streetActivityDOS = sweepStreetActivityMapper.find(streetActivityQuery);
        if (!CollectionUtils.isEmpty(streetActivityDOS)) {
            return ApiResultWrapper.fail(GroupServiceDayErrorCodes.STARTED_ACTIVITY_EXIT);
        }
        return null;
    }


    /**
     * 更新父活动状态
     *
     * @param status
     */
    private void updateParentStatus(SweepStreetActivityDO sweepStreetActivityDO, Integer status, Date time) {

        ParentSweepStreetActivityDO parentActivityDo = new ParentSweepStreetActivityDO();
        //子活动有一个开始，父活动即为开始
        if (status == SweepStreetStatusEnum.PROCESSING.getId()) {
            //判断父活动是否已开始
            ParentSweepStreetActivityDO parentSweepStreetActivityDO = getParentGroupServiceDayById(sweepStreetActivityDO.getParentId());
            if (parentSweepStreetActivityDO.getStatus().equals(SweepStreetStatusEnum.NOT_START.getId())) {
                parentActivityDo.setId(sweepStreetActivityDO.getParentId());
                parentActivityDo.setStatus(SweepStreetStatusEnum.PROCESSING.getId());
                parentActivityDo.setRealStartTime(time);
            }
        } else if (status == SweepStreetStatusEnum.END.getId() ||
                status == SweepStreetStatusEnum.ABNORMAL_END.getId()
                || status == SweepStreetStatusEnum.CANCEL.getId()
                || status == SweepStreetStatusEnum.AUTO_END.getId()) {
            //所有子活动结束，父活动即为结束
            SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
            streetActivityQuery.setParentId(sweepStreetActivityDO.getParentId());
            List<SweepStreetActivityDO> streetActivityDOS = sweepStreetActivityMapper.find(streetActivityQuery);
            List<SweepStreetActivityDO> notEndList = streetActivityDOS.stream().
                    filter(a -> a.getStatus().equals(SweepStreetStatusEnum.NOT_START.getId()) ||
                            a.getStatus().equals(SweepStreetStatusEnum.PROCESSING.getId())).
                    collect(Collectors.toList());
            if (CollectionUtils.isEmpty(notEndList)) {
                //更新父活动状态未已结束
                parentActivityDo.setStatus(SweepStreetStatusEnum.END.getId());
                parentActivityDo.setId(sweepStreetActivityDO.getParentId());
                parentActivityDo.setRealEndTime(time);
            }
        }

        parentSweepStreetActivityMapper.update(parentActivityDo);
    }

    private ParentSweepStreetActivityDO getParentGroupServiceDayById(Long id) {
        ParentSweepStreetActivityQuery parentSweepStreetActivityQuery = new ParentSweepStreetActivityQuery();
        parentSweepStreetActivityQuery.setId(id);
        ParentSweepStreetActivityDO parentActivity = parentSweepStreetActivityMapper.get(parentSweepStreetActivityQuery);
        return parentActivity;
    }

    /**
     * 拼装活动打卡位置信息
     * @param vos
     */
    private void buildLocation(List<SweepStreetActivityVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        List<Long> activityIds = vos.stream().map(SweepStreetActivityVO::getId).collect(Collectors.toList());
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setActivityIds(activityIds);
        signRecordQuery.setBizType(5);
        List<SignRecordDO> signRecordDOS = signRecordMapper.find(signRecordQuery);
        if (CollectionUtils.isEmpty(signRecordDOS)) {
            return;
        }
        Map<Long, List<SignRecordDO>> listMap = signRecordDOS.stream().collect(Collectors.groupingBy(SignRecordDO::getActivityId));
        for (SweepStreetActivityVO activityVO:vos) {
            List<SignRecordDO> recordDOS = listMap.get(activityVO.getId());
            if (!CollectionUtils.isEmpty(recordDOS)) {
                LocationDetailVO locationDetailVO = GsonUtils.fromGson2Obj(recordDOS.get(0).getStartLocation(), LocationDetailVO.class);
                activityVO.setLocation(locationDetailVO.getLocation());
                activityVO.setAddress(locationDetailVO.getAddress());
            }
        }
    }



    private void syncCreateSweepStreetActivity(SweepStreetActivityRequest request, ParentSweepStreetActivityDO parentSweepStreetActivityDO, List<SweepStreetActivityDO> childActivityList) {
        HuaWeiCreateSweepStreetActivityRequest huaWeiRequest = new HuaWeiCreateSweepStreetActivityRequest();
        huaWeiRequest.setParentActivityId(SweepStreetActivityConstants.ID_PREFIX + parentSweepStreetActivityDO.getId());
        huaWeiRequest.setTitle(request.getTitle());
        huaWeiRequest.setStartTime(DateUtil.formatDateTime(parentSweepStreetActivityDO.getPlanStartTime()));
        huaWeiRequest.setEndTime(DateUtil.formatDateTime(parentSweepStreetActivityDO.getPlanEndTime()));
        huaWeiRequest.setStatus(String.valueOf(parentSweepStreetActivityDO.getStatus()));

        List<HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity> childSweepStreetActivityArrayList = new ArrayList<>();

        for (SweepStreetActivityDO sweepStreetActivityDO : childActivityList) {
            HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity childSweepStreetActivity = new HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity();
            childSweepStreetActivity.setActivityId(SweepStreetActivityConstants.ID_PREFIX + sweepStreetActivityDO.getId());
            List<HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity.Participant> participantList = new ArrayList<>();
            List<SweepStreetActivityRequest.PartnerBean> partnerBeanList = GsonUtils.fromJsonToList(sweepStreetActivityDO.getPartner(), SweepStreetActivityRequest.PartnerBean[].class);
            for (SweepStreetActivityRequest.PartnerBean partnerBean : partnerBeanList) {
                HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity.Participant participant = new HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity.Participant();
                //判断用户来源
                if (StrUtil.isNotBlank(partnerBean.getUserId())) {
                    //来自网格
                    participant.setUserSource("1");
                    participant.setUserId(partnerBean.getUserId());
                } else {
                    //来自通讯录
                    participant.setUserSource("2");
                    participant.setUserName(partnerBean.getName());
                    participant.setUserPhone(partnerBean.getMobile());
                }

                //判断参与人类型
                if (partnerBean.getMobile().equals(sweepStreetActivityDO.getMobile())) {
                    //负责人
                    participant.setUserType("1");
                } else {
                    //参与人
                    participant.setUserType("2");
                }

                participantList.add(participant);
            }
            childSweepStreetActivity.setParticipantList(participantList);
            childSweepStreetActivityArrayList.add(childSweepStreetActivity);
        }

        huaWeiRequest.setChildrenList(childSweepStreetActivityArrayList);
        Map<String, Object> map = BeanUtil.beanToMap(huaWeiRequest, false, true);
        log.info("[syncCreateSweepStreetActivuty] 新建扫村活动同步华为,请求参数:{}", GsonUtils.toJson(map));
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.CREATE_SWEEP_STREET_ACTIVITY.getApiName(), SmartGridContext.getMobile());
    }



    private SweepStreetActivityDO  getSweepStreetActivityDO(ParentSweepStreetActivityDO parentSweepStreetActivityDO, SweepStreetActivityRequest.PartnerBean partnerBean) {
        SweepStreetActivityDO sweepStreetActivityDO = new SweepStreetActivityDO();
        sweepStreetActivityDO.setParentId(parentSweepStreetActivityDO.getId());
        sweepStreetActivityDO.setTitle(parentSweepStreetActivityDO.getTitle());
        sweepStreetActivityDO.setAddress(parentSweepStreetActivityDO.getAddress());
        sweepStreetActivityDO.setCreatorId(parentSweepStreetActivityDO.getCreatorId());
        sweepStreetActivityDO.setCreatorName(parentSweepStreetActivityDO.getCreatorName());
        sweepStreetActivityDO.setPlanStartTime(parentSweepStreetActivityDO.getPlanStartTime());
        sweepStreetActivityDO.setPlanEndTime(parentSweepStreetActivityDO.getPlanEndTime());
        sweepStreetActivityDO.setLocation(parentSweepStreetActivityDO.getLocation());
        sweepStreetActivityDO.setPartner(GsonUtils.toJson(parentSweepStreetActivityDO.getPartner()));
        sweepStreetActivityDO.setStatus(GroupServiceDayStatusEnum.NOT_START.getId());
        sweepStreetActivityDO.setMobile(partnerBean.getMobile());
        sweepStreetActivityDO.setName(partnerBean.getName());
        return sweepStreetActivityDO;
    }


    private ParentSweepStreetActivityDO getParentSweepStreetActivityDO(SweepStreetActivityRequest request) {
        ParentSweepStreetActivityDO parentSweepStreetActivityDO = new ParentSweepStreetActivityDO();
        parentSweepStreetActivityDO.setTitle(request.getTitle());

        parentSweepStreetActivityDO.setAddress(request.getAddress());
        parentSweepStreetActivityDO.setCreatorId(Long.valueOf(SmartGridContext.getUid()));
        parentSweepStreetActivityDO.setCreatorOrgId(Long.valueOf(SmartGridContext.getOrgId()));
        parentSweepStreetActivityDO.setCreatorName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        parentSweepStreetActivityDO.setMobile(SmartGridContext.getMobile());
        parentSweepStreetActivityDO.setPlanStartTime(new Date(request.getPlanStartTime()));
        parentSweepStreetActivityDO.setPlanEndTime(new Date(request.getPlanEndTime()));
        parentSweepStreetActivityDO.setLocation(request.getLocation());
        parentSweepStreetActivityDO.setPartner(GsonUtils.toJson(request.getPartner()));
        parentSweepStreetActivityDO.setStatus(GroupServiceDayStatusEnum.NOT_START.getId());
        try {
            parentSweepStreetActivityDO.setGridId(SmartGridContext.getSelectGridUserRoleDetail().getId());
        } catch (Exception e) {
            log.error("[createSweepStreetActivity] 该用户无网格,mobile:{}", SmartGridContext.getMobile());
            parentSweepStreetActivityDO.setGridId("0");
        }
        return parentSweepStreetActivityDO;
    }


}
