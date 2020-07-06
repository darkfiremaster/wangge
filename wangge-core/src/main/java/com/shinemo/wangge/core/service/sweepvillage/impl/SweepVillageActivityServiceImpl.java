package com.shinemo.wangge.core.service.sweepvillage.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.shinemo.client.common.StatusEnum;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.exception.CommonErrorCodes;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepvillage.domain.model.SweepVillageActivityDO;
import com.shinemo.sweepvillage.domain.model.SweepVillageMarketingNumberDO;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageActivityQuery;
import com.shinemo.sweepvillage.domain.query.SweepVillageMarketingNumberQuery;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.domain.request.SweepVillageBusinessRequest;
import com.shinemo.sweepvillage.domain.vo.*;
import com.shinemo.sweepvillage.enums.SweepVillageStatusEnum;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.SignRecordMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageMarketingNumberMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageVisitRecordingMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:32
 * @Desc
 */
@Service
@Slf4j
public class SweepVillageActivityServiceImpl implements SweepVillageActivityService {

    @Resource
    private SweepVillageActivityMapper sweepVillageActivityMapper;

    @Autowired
    private ThirdApiMappingService thirdApiMappingService;

    @Resource
    private SweepVillageMarketingNumberMapper sweepVillageMarketingNumberMapper;

    @Resource
    private SweepVillageVisitRecordingMapper sweepVillageVisitRecordingMapper;

    @Resource
    private SignRecordMapper signRecordMapper;

    @Resource
    private StallUpConfig stallUpConfig;


    private static final Integer WEEK_TYPE = 1;
    private static final Integer MONTH_TYPE = 2;


    @Override
    public ApiResult<Map<String, Object>> createVillage(VillageVO villageVO) {
        //校验参数
        Assert.notNull(villageVO, "request is null");
        Assert.hasText(villageVO.getName(), "name is null");
        Assert.hasText(villageVO.getArea(), "area is null");
        Assert.hasText(villageVO.getAreaCode(), "areaCode is null");
        Assert.hasText(villageVO.getLocation(), "location is null");
        Assert.hasText(villageVO.getRgsLocation(), "rgsLocation is null");

        //透传华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", villageVO.getName());
        map.put("gridId", SmartGridContext.getSelectGridUserRoleDetail().getId());
        map.put("area", villageVO.getArea());
        map.put("areaCode", villageVO.getAreaCode());
        map.put("location", villageVO.getRgsLocation());
        map.put("mobile", SmartGridContext.getMobile());
        map.put("createTime", System.currentTimeMillis());
        ApiResult<Map<String, Object>> result = thirdApiMappingService.dispatch(map, "addVillage");
        if (result.isSuccess()) {
            log.info("[createVillage] 新建村庄成功");
        } else {
            log.error("[createVillage] 新建村庄失败,失败原因:{},请求参数:{}", result.getMsg(), map);
        }
        return result;
    }

    @Override
    public ApiResult<Map<String, Object>> getVillageList() {
        //透传华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("gridId", SmartGridContext.getSelectGridUserRoleDetail().getId());
        map.put("mobile", SmartGridContext.getMobile());
        ApiResult<Map<String, Object>> result = thirdApiMappingService.dispatch(map, "queryVillageList");
        return result;
    }

    @Override
    @Transactional
    public ApiResult<Void> createSweepVillageActivity(SweepVillageActivityVO sweepVillageActivityVO) {
        //校验参数
        Assert.notNull(sweepVillageActivityVO, "sweepVillageActivityVO is null");
        Assert.notNull(sweepVillageActivityVO.getTitle(), "title is null");
        Assert.notNull(sweepVillageActivityVO.getVillageId(), "villageId is null");
        Assert.notNull(sweepVillageActivityVO.getVillageName(), "villageName is null");
        Assert.notNull(sweepVillageActivityVO.getArea(), "area is null");
        Assert.notNull(sweepVillageActivityVO.getAreaCode(), "areaCode is null");
        Assert.notNull(sweepVillageActivityVO.getLocation(), "location is null");
        Assert.notNull(sweepVillageActivityVO.getRgsLocation(), "rgsLocation is null");
        Assert.notNull(sweepVillageActivityVO.getRgsLocation(), "startLocation is null");

        //判断用户是否已有进行中的扫村活动
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
        sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
        SweepVillageActivityDO db = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (db != null) {
            log.info("[createSweepVillageActivity]扫村活动已存在,query:{}", sweepVillageActivityQuery);
            throw new ApiException(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_PROCESSING_EXIST);
        }

        Date startTime = new Date();
        SweepVillageActivityDO sweepVillageActivityDO = new SweepVillageActivityDO();
        sweepVillageActivityDO.setTitle(sweepVillageActivityVO.getTitle());
        sweepVillageActivityDO.setVillageId(sweepVillageActivityVO.getVillageId());
        sweepVillageActivityDO.setVillageName(sweepVillageActivityVO.getVillageName());
        sweepVillageActivityDO.setArea(sweepVillageActivityVO.getArea());
        sweepVillageActivityDO.setAreaCode(sweepVillageActivityVO.getAreaCode());
        sweepVillageActivityDO.setLocation(sweepVillageActivityVO.getLocation());
        sweepVillageActivityDO.setRgsLocation(sweepVillageActivityVO.getRgsLocation());
        sweepVillageActivityDO.setGridId(SmartGridContext.getSelectGridUserRoleDetail().getId());
        sweepVillageActivityDO.setMobile(SmartGridContext.getMobile());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
        sweepVillageActivityDO.setStartTime(startTime);
        sweepVillageActivityMapper.insert(sweepVillageActivityDO);


        SignRecordDO signRecordDO = new SignRecordDO();
        signRecordDO.setUserId(SmartGridContext.getUid());
        signRecordDO.setActivityId(sweepVillageActivityDO.getId());
        signRecordDO.setStartTime(startTime);
        signRecordDO.setBizType(SignRecordBizTypeEnum.SWEEP_VILLAGE.getId());
        signRecordDO.setStartLocation(sweepVillageActivityVO.getStartLocation());
        signRecordMapper.insert(signRecordDO);

        //同步华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("villageId", sweepVillageActivityVO.getVillageId());
        map.put("mobile", SmartGridContext.getMobile());
        map.put("title", sweepVillageActivityVO.getTitle());
        map.put("status", SweepVillageStatusEnum.PROCESSING.getId());
        map.put("activityId", String.valueOf(sweepVillageActivityDO.getId()));
        map.put("createTime", startTime.getTime());
        map.put("updateTime", startTime.getTime());
        map.put("startTime", startTime.getTime());
        map.put("gridId", SmartGridContext.getSelectGridUserRoleDetail().getId());
        thirdApiMappingService.asyncDispatch(map, "createSweepVillagePlan", SmartGridContext.getMobile());
        log.info("[createSweepVillageActivity] 新建扫村活动成功");
        return ApiResult.of(0);
    }


    @Override
    @Transactional
    public ApiResult<Void> finishActivity(SweepVillageSignVO sweepVillageSignVO) {
        //校验参数
        Assert.notNull(sweepVillageSignVO.getActivityId(), "id is null");
        Assert.notNull(sweepVillageSignVO.getEndLocation(), "endLocation is null");
        Date endTime = new Date();
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(sweepVillageSignVO.getActivityId());
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST);
        }
        if (!sweepVillageActivityDO.getStatus().equals(SweepVillageStatusEnum.PROCESSING.getId())) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_STATUS_ERROR);
        }
        //判断打卡距离
        Integer distance = DistanceUtils.getDistance(sweepVillageActivityDO.getLocation(), sweepVillageSignVO.getEndLocation());
        if (distance >= 10000) {
            //异常打卡
            log.info("[finishActivity]异常打卡,超过打卡距离,活动id:{},距离:{}", sweepVillageSignVO.getActivityId(), distance);
            sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.ABNORMAL_END.getId());
        } else {
            sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.END.getId());
        }

        sweepVillageActivityDO.setEndTime(endTime);
        sweepVillageActivityMapper.update(sweepVillageActivityDO);

        //修改签到表
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setUserId(SmartGridContext.getUid());
        signRecordQuery.setBizType(SignRecordBizTypeEnum.SWEEP_VILLAGE.getId());
        signRecordQuery.setActivityId(sweepVillageSignVO.getActivityId());
        SignRecordDO signRecordFromDB = signRecordMapper.get(signRecordQuery);
        signRecordFromDB.setEndTime(endTime);
        signRecordFromDB.setEndLocation(sweepVillageSignVO.getEndLocation());
        signRecordFromDB.setRemark(sweepVillageSignVO.getRemark());
        signRecordFromDB.setImgUrl(sweepVillageSignVO.getImgUrl());
        signRecordMapper.update(signRecordFromDB);

        //同步华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("activityId", String.valueOf(sweepVillageSignVO.getActivityId()));
        map.put("mobile", SmartGridContext.getMobile());
        map.put("status", sweepVillageActivityDO.getStatus());
        map.put("updateTime", endTime.getTime());
        map.put("endTime", endTime.getTime());
        if (StrUtil.isNotBlank(sweepVillageSignVO.getRemark())) {
            map.put("remark", sweepVillageSignVO.getRemark());
        }
        if (StrUtil.isNotBlank(sweepVillageSignVO.getImgUrl())) {
            List<String> imgUrlList = Convert.toList(String.class, sweepVillageSignVO.getImgUrl());
            map.put("picUrl", imgUrlList);
        }
        ApiResult<Map<String, Object>> result = thirdApiMappingService.asyncDispatch(map, "updateSweepVillagePlan", SmartGridContext.getMobile());

        log.info("[finishActivity] 结束扫村成功,活动id:{}", sweepVillageSignVO.getActivityId());
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<List<SweepVillageActivityResultVO>> getSweepVillageActivityList(SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest) {
        //校验参数
        Assert.notNull(sweepVillageActivityQueryRequest, "request is null");
        Assert.notNull(sweepVillageActivityQueryRequest.getStatus(), "status is null");

        if (sweepVillageActivityQueryRequest.getStatus().equals(SweepVillageStatusEnum.PROCESSING.getId())) {
            //查进行中的活动
            SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
            sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
            sweepVillageActivityQuery.setStatus(SweepVillageStatusEnum.PROCESSING.getId());

            log.info("[getSweepVillageActivityList] 获取进行中的扫村活动列表,query:{}", sweepVillageActivityQuery);
            List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);
            if (sweepVillageActivityDOS == null) {
                log.error("[getSweepVillageActivityList] find error,query:{}", sweepVillageActivityQuery);
                return ApiResult.fail(CommonErrorCodes.SERVER_ERROR.code);
            }
            //do转为vo
            List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
            for (SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS) {
                SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
                resultVO.setStartTime(sweepVillageActivityDO.getStartTime());
                resultVO.setArea(sweepVillageActivityDO.getArea());
                resultVO.setVillageId(sweepVillageActivityDO.getVillageId());
                resultVO.setVillageName(sweepVillageActivityDO.getVillageName());
                resultVO.setSweepVillageActivityId(sweepVillageActivityDO.getId());
                resultVOList.add(resultVO);
            }
            return ApiResult.of(0, resultVOList);
        }

        if (sweepVillageActivityQueryRequest.getStatus().equals(SweepVillageStatusEnum.END.getId())) {
            Assert.notNull(sweepVillageActivityQueryRequest.getStartTime(), "startTime is null");
            Assert.notNull(sweepVillageActivityQueryRequest.getEndTime(), "endTime is null");

            //查已结束的活动
            SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
            sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
            sweepVillageActivityQuery.setStatusList(Lists.newArrayList(
                    SweepVillageStatusEnum.END.getId(),
                    SweepVillageStatusEnum.ABNORMAL_END.getId()
            ));
            sweepVillageActivityQuery.setStartTime(sweepVillageActivityQueryRequest.getStartTime());
            sweepVillageActivityQuery.setEndTime(sweepVillageActivityQueryRequest.getEndTime());
            sweepVillageActivityQuery.setOrderByEnable(true);
            sweepVillageActivityQuery.putOrderBy("end_time", false);

            log.info("[getSweepVillageActivityList] 获取已结束的扫村活动列表,query:{}", sweepVillageActivityQuery);
            List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);
            //do 转化为vo
            List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
            for (SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS) {
                SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
                BeanUtils.copyProperties(sweepVillageActivityDO, resultVO);

                //获取统计量
                SweepVillageMarketingNumberQuery query = new SweepVillageMarketingNumberQuery();
                query.setActivityId(sweepVillageActivityDO.getId());
                query.setMobile(sweepVillageActivityDO.getMobile());
                SweepVillageMarketingNumberDO sweepVillageMarketingNumberDO = sweepVillageMarketingNumberMapper.get(query);
                if (sweepVillageMarketingNumberDO == null) {
                    log.error("[getSweepVillageActivityList] query market num error,query:{}", query);
                    continue;
                }

                resultVO.setHandleCount(sweepVillageMarketingNumberDO.getCount());

                //获取走访户数
                SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
                visitRecordingQuery.setActivityId(sweepVillageActivityDO.getId());
                long count = sweepVillageVisitRecordingMapper.count(visitRecordingQuery);
                resultVO.setVisitCount((int) count);
                resultVOList.add(resultVO);
            }
            return ApiResult.of(0, resultVOList);
        }

        throw new ApiException("illegal status", 500);
    }

    @Override
    public ApiResult<SweepVillageActivityFinishVO> getFinishResultInfo(Integer type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();

        Date startTime = new Date();
        SweepVillageActivityQuery query = new SweepVillageActivityQuery();
        if (WEEK_TYPE.equals(type)) {
            startTime = DateUtils.getThisWeekMonday(zero);
        } else if (MONTH_TYPE.equals(type)) {
            startTime = DateUtils.getThisMonthFirstDay();
        }
        //1.获取指定时间段内的已结束的活动列表 指定手机号
        query.setMobile(SmartGridContext.getMobile());
        query.setStartTime(startTime);
        query.setStatusList(Lists.newArrayList(
                SweepVillageStatusEnum.END.getId(),
                SweepVillageStatusEnum.ABNORMAL_END.getId()
        ));
        query.setEndTime(new Date());

        log.info("[getSweepVillageActivityList] 获取已结束的扫村活动列表,query:{}", query);
        List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(query);

        Set<Long> activityIdSet = new HashSet<>();
        sweepVillageActivityDOS.forEach(val -> {
            activityIdSet.add(val.getId());
        });
        //2.将指定时间内的活动中生成的走访记录加载到内存
        SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
        visitRecordingQuery.setFilterCreateTime(true);
        visitRecordingQuery.setStartTime(startTime);
        visitRecordingQuery.setEndTime(new Date());
        visitRecordingQuery.setMobile(SmartGridContext.getMobile());
        visitRecordingQuery.setStatus(StatusEnum.NORMAL.getId());
        List<SweepVillageVisitRecordingDO> visitRecordingDOS = sweepVillageVisitRecordingMapper.find(visitRecordingQuery);

        int visitCount = 0;
        for (SweepVillageVisitRecordingDO visitRecordingDO : visitRecordingDOS) {
            if (activityIdSet.contains(visitRecordingDO.getActivityId())) {
                visitCount++;
            }

        }

        SweepVillageActivityFinishVO sweepVillageActivityFinishVO = new SweepVillageActivityFinishVO();
        sweepVillageActivityFinishVO.setSweepVillageCount(sweepVillageActivityDOS.size());
        sweepVillageActivityFinishVO.setVisitUserCount(visitCount);
        return ApiResult.of(0, sweepVillageActivityFinishVO);
    }

    @Override
    public ApiResult<Void> enterMarketingNumber(SweepVillageBusinessRequest request) {

        ApiResult apiResult = checkActivityExist(request.getActivityId());
        if (!apiResult.isSuccess()) {
            return apiResult;
        }

        SweepVillageMarketingNumberQuery numberQuery = new SweepVillageMarketingNumberQuery();
        numberQuery.setActivityId(request.getActivityId());
        SweepVillageMarketingNumberDO marketingNumberDO = sweepVillageMarketingNumberMapper.get(numberQuery);
        if (marketingNumberDO == null) {
            //添加
            SweepVillageMarketingNumberDO newMarketingNumberDO = new SweepVillageMarketingNumberDO();
            newMarketingNumberDO.setActivityId(request.getActivityId());
            if (!CollectionUtils.isEmpty(request.getBizList())) {
                newMarketingNumberDO.setCount(request.getBizList().stream().mapToInt(v -> v.getNum()).sum());
                newMarketingNumberDO.setDetail(GsonUtils.toJson(request.getBizList()));
            } else {
                newMarketingNumberDO.setCount(0);
                List<StallUpBizType> sweepVillageBizList = stallUpConfig.getConfig().getSweepVillageBizList();
                List<SweepVillageBizDetail> details = new ArrayList<>();
                for (StallUpBizType bizType : sweepVillageBizList) {
                    SweepVillageBizDetail bizDetail = new SweepVillageBizDetail();
                    bizDetail.setId(bizType.getId());
                    bizDetail.setName(bizType.getName());
                    bizDetail.setNum(0);
                    details.add(bizDetail);
                }
                newMarketingNumberDO.setDetail(GsonUtils.toJson(details));
            }
            newMarketingNumberDO.setRemark(request.getRemark());
            newMarketingNumberDO.setUserId(SmartGridContext.getUid());
            sweepVillageMarketingNumberMapper.insert(newMarketingNumberDO);
        } else {
            //更新
            if (!CollectionUtils.isEmpty(request.getBizList())) {
                marketingNumberDO.setCount(request.getBizList().stream().mapToInt(v -> v.getNum()).sum());
                marketingNumberDO.setDetail(GsonUtils.toJson(request.getBizList()));
            } else {
                marketingNumberDO.setCount(0);
                List<StallUpBizType> sweepVillageBizList = stallUpConfig.getConfig().getSweepVillageBizList();
                List<SweepVillageBizDetail> details = new ArrayList<>();
                for (StallUpBizType bizType : sweepVillageBizList) {
                    SweepVillageBizDetail bizDetail = new SweepVillageBizDetail();
                    bizDetail.setId(bizType.getId());
                    bizDetail.setName(bizType.getName());
                    bizDetail.setNum(0);
                    details.add(bizDetail);
                }
                marketingNumberDO.setDetail(GsonUtils.toJson(details));
            }
            marketingNumberDO.setRemark(request.getRemark());
            sweepVillageMarketingNumberMapper.update(marketingNumberDO);
        }
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<SweepVillageBusinessRequest> getMarketingNumber(Long activityId) {
        //校验活动是否存在
        ApiResult apiResult = checkActivityExist(activityId);
        if (!apiResult.isSuccess()) {
            return apiResult;
        }


        SweepVillageMarketingNumberQuery numberQuery = new SweepVillageMarketingNumberQuery();
        numberQuery.setActivityId(activityId);
        SweepVillageMarketingNumberDO marketingNumberDO = sweepVillageMarketingNumberMapper.get(numberQuery);
        SweepVillageBusinessRequest numberVO = new SweepVillageBusinessRequest();
        if (marketingNumberDO == null) {
            List<StallUpBizType> sweepVillageBizList = stallUpConfig.getConfig().getSweepVillageBizList();
            List<SweepVillageBizDetail> details = new ArrayList<>();
            for (StallUpBizType bizType : sweepVillageBizList) {
                SweepVillageBizDetail bizDetail = new SweepVillageBizDetail();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }
            numberVO.setBizList(details);
        } else {
            String detail = marketingNumberDO.getDetail();
            if (!StringUtils.isBlank(detail)) {
                List<SweepVillageBizDetail> details = GsonUtils.fromJsonToList(detail, SweepVillageBizDetail[].class);
                numberVO.setBizList(details);
            }
            numberVO.setRemark(marketingNumberDO.getRemark());
        }

        //用户姓名从哪来？
        numberVO.setUsername(SmartGridContext.getUserName());
        return ApiResult.of(0, numberVO);
    }


    private ApiResult<SweepVillageActivityDO> checkActivityExist(Long activityId){
        //判断扫村活动是否存在
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(activityId);
        SweepVillageActivityDO activityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (activityDO == null) {
            log.error("[checkActivityExist] error,query:{}",sweepVillageActivityQuery);
            return null;
        }
        return ApiResult.of(0,activityDO);
    }
}


