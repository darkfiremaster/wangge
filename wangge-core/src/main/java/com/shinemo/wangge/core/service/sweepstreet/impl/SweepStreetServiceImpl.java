package com.shinemo.wangge.core.service.sweepstreet.impl;

import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.error.GroupServiceDayErrorCodes;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import com.shinemo.sweepstreet.domain.model.ParentSweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetMarketingNumberDO;
import com.shinemo.sweepstreet.domain.query.ParentSweepStreetActivityQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetActivityQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetMarketingNumberQuery;
import com.shinemo.sweepstreet.domain.request.SweepStreetListRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetSignRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import com.shinemo.sweepstreet.enums.SweepStreetStatusEnum;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
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


}
