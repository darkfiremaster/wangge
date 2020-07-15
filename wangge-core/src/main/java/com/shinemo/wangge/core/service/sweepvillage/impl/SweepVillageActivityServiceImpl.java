package com.shinemo.wangge.core.service.sweepvillage.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shinemo.client.common.ListVO;
import com.shinemo.client.common.StatusEnum;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.operate.query.UserOperateLogQuery;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepvillage.domain.constant.SweepVillageConstants;
import com.shinemo.sweepvillage.domain.enums.HuaweiSweepVillageUrlEnum;
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
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
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
    private UserOperateLogMapper userOperateLogMapper;

    @Resource
    private SignRecordMapper signRecordMapper;

    @Resource
    private StallUpConfig stallUpConfig;


    private static final Integer WEEK_TYPE = 1;
    private static final Integer MONTH_TYPE = 2;


    @Override
    public ApiResult<VillageVO> getUserLastVillage() {
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.getUserLastVillage(sweepVillageActivityQuery);
        if (sweepVillageActivityDO != null) {
            VillageVO villageVO = new VillageVO();
            villageVO.setId(sweepVillageActivityDO.getVillageId());
            villageVO.setName(sweepVillageActivityDO.getVillageName());
            villageVO.setArea(sweepVillageActivityDO.getArea());
            villageVO.setAreaCode(sweepVillageActivityDO.getAreaCode());
            villageVO.setLocation(sweepVillageActivityDO.getLocation());
            villageVO.setOriginLocation(sweepVillageActivityDO.getOriginLocation());
            log.info("[getUserLastVillage]用户上次打卡的村庄信息:{}", villageVO);
            return ApiResult.of(0, villageVO);
        }
        return ApiResult.of(0, null);
    }

    @Override
    public ApiResult<Map<String, Object>> createVillage(VillageVO villageVO) {
        //校验参数
        Assert.notNull(villageVO, "request is null");
        Assert.hasText(villageVO.getName(), "name is null");
        Assert.hasText(villageVO.getArea(), "area is null");
        Assert.hasText(villageVO.getAreaCode(), "areaCode is null");
        Assert.hasText(villageVO.getLocation(), "location is null");
        Assert.hasText(villageVO.getOriginLocation(), "originLocation is null");

        //透传华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", villageVO.getName());
        map.put("gridId", SmartGridContext.getSelectGridUserRoleDetail().getId());
        map.put("area", villageVO.getArea());
        map.put("areaCode", villageVO.getAreaCode());
        map.put("location", villageVO.getLocation());
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
        Assert.hasText(sweepVillageActivityVO.getTitle(), "title is null");
        Assert.hasText(sweepVillageActivityVO.getVillageId(), "villageId is null");
        Assert.hasText(sweepVillageActivityVO.getVillageName(), "villageName is null");
        Assert.hasText(sweepVillageActivityVO.getArea(), "area is null");
        Assert.hasText(sweepVillageActivityVO.getAreaCode(), "areaCode is null");
        Assert.hasText(sweepVillageActivityVO.getLocation(), "location is null");
        Assert.hasText(sweepVillageActivityVO.getOriginLocation(), "originLocation is null");
        Assert.notNull(sweepVillageActivityVO.getLocationDetailVO(), "locationDetail is null");
        Assert.hasText(sweepVillageActivityVO.getLocationDetailVO().getLocation(), "location is null");
        Assert.hasText(sweepVillageActivityVO.getLocationDetailVO().getAddress(), "address is null");

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
        sweepVillageActivityDO.setAddress(sweepVillageActivityVO.getLocationDetailVO().getAddress());
        sweepVillageActivityDO.setArea(sweepVillageActivityVO.getArea());
        sweepVillageActivityDO.setAreaCode(sweepVillageActivityVO.getAreaCode());
        sweepVillageActivityDO.setLocation(sweepVillageActivityVO.getLocation());
        sweepVillageActivityDO.setOriginLocation(sweepVillageActivityVO.getOriginLocation());
        sweepVillageActivityDO.setGridId(SmartGridContext.getSelectGridUserRoleDetail().getId());
        sweepVillageActivityDO.setMobile(SmartGridContext.getMobile());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
        sweepVillageActivityDO.setStartTime(startTime);
        sweepVillageActivityDO.setCreatorName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        sweepVillageActivityMapper.insert(sweepVillageActivityDO);


        SignRecordDO signRecordDO = new SignRecordDO();
        signRecordDO.setUserId(SmartGridContext.getUid());
        signRecordDO.setActivityId(sweepVillageActivityDO.getId());
        signRecordDO.setStartTime(startTime);
        signRecordDO.setBizType(SignRecordBizTypeEnum.SWEEP_VILLAGE.getId());
        signRecordDO.setStartLocation(GsonUtils.toJson(sweepVillageActivityVO.getLocationDetailVO()));
        signRecordMapper.insert(signRecordDO);

        //同步华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("villageId", sweepVillageActivityVO.getVillageId());
        map.put("mobile", SmartGridContext.getMobile());
        map.put("title", sweepVillageActivityVO.getTitle());
        map.put("status", SweepVillageStatusEnum.PROCESSING.getId());
        map.put("activityId", SweepVillageConstants.ID_PREFIX + sweepVillageActivityDO.getId());
        map.put("createTime", startTime.getTime());
        map.put("updateTime", startTime.getTime());
        map.put("startTime", startTime.getTime());
        map.put("gridId", SmartGridContext.getSelectGridUserRoleDetail().getId());
        //todo 23号联调 同步经纬度
        thirdApiMappingService.asyncDispatch(map, "createSweepVillagePlan", SmartGridContext.getMobile());
        log.info("[createSweepVillageActivity] 新建扫村活动成功,活动id:{}", sweepVillageActivityDO.getId());
        return ApiResult.of(0);
    }


    @Override
    @Transactional
    public ApiResult<Void> finishActivity(SweepVillageSignVO sweepVillageSignVO) {
        //校验参数
        Assert.notNull(sweepVillageSignVO.getSweepVillageActivityId(), "id is null");
        Assert.notEmpty(sweepVillageSignVO.getPicUrl(), "图片不能为空");
        Assert.notNull(sweepVillageSignVO.getLocationDetailVO(), "locationDetail is null");
        Assert.hasText(sweepVillageSignVO.getLocationDetailVO().getLocation(), "location is null");
        Date endTime = new Date();
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(sweepVillageSignVO.getSweepVillageActivityId());
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST);
        }
        if (!sweepVillageActivityDO.getStatus().equals(SweepVillageStatusEnum.PROCESSING.getId())) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_STATUS_ERROR);
        }
        //判断打卡距离
        Integer distance = DistanceUtils.getDistance(sweepVillageActivityDO.getLocation(), sweepVillageSignVO.getLocationDetailVO().getLocation());
        if (distance >= 10000) {
            //异常打卡
            log.info("[finishActivity]异常打卡,超过打卡距离,活动id:{},距离:{}", sweepVillageSignVO.getSweepVillageActivityId(), distance);
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
        signRecordQuery.setActivityId(sweepVillageSignVO.getSweepVillageActivityId());
        SignRecordDO signRecordFromDB = signRecordMapper.get(signRecordQuery);
        signRecordFromDB.setEndTime(endTime);
        signRecordFromDB.setEndLocation(GsonUtils.toJson(sweepVillageSignVO.getLocationDetailVO()));
        signRecordFromDB.setRemark(sweepVillageSignVO.getRemark());
        signRecordFromDB.setImgUrl(GsonUtils.toJson(sweepVillageSignVO.getPicUrl()));
        signRecordMapper.update(signRecordFromDB);

        //同步华为
        HashMap<String, Object> map = new HashMap<>();
        map.put("activityId", SweepVillageConstants.ID_PREFIX + sweepVillageSignVO.getSweepVillageActivityId());
        map.put("mobile", SmartGridContext.getMobile());
        map.put("status", sweepVillageActivityDO.getStatus());
        map.put("updateTime", endTime.getTime());
        map.put("endTime", endTime.getTime());
        if (StrUtil.isNotBlank(sweepVillageSignVO.getRemark())) {
            map.put("remark", sweepVillageSignVO.getRemark());
        }
        map.put("picUrl", sweepVillageSignVO.getPicUrl());
        //todo 23号联调 同步经纬度
        thirdApiMappingService.asyncDispatch(map, "updateSweepVillagePlan", SmartGridContext.getMobile());
        log.info("[finishActivity] 结束扫村成功,活动id:{}", sweepVillageSignVO.getSweepVillageActivityId());
        return ApiResult.of(0);
    }

    /**
     * 默认分页
     *
     * @param sweepVillageActivityQueryRequest
     * @return
     */
    @Override
    public ApiResult<ListVO<SweepVillageActivityResultVO>> getSweepVillageActivityList(SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest) {
        //校验参数
        Assert.notNull(sweepVillageActivityQueryRequest, "request is null");
        Assert.notNull(sweepVillageActivityQueryRequest.getStatus(), "status is null");

        Long currentPage = sweepVillageActivityQueryRequest.getCurrentPage();
        Long pageSize = sweepVillageActivityQueryRequest.getPageSize();

        if (sweepVillageActivityQueryRequest.getStatus().equals(SweepVillageStatusEnum.PROCESSING.getId())) {
            //查进行中的活动
            SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
            sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
            sweepVillageActivityQuery.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
            sweepVillageActivityQuery.setPageEnable(false);
            if (sweepVillageActivityQueryRequest.getCurrentPage() != null) {
                sweepVillageActivityQuery.setPageEnable(true);
                sweepVillageActivityQuery.setPageSize(sweepVillageActivityQueryRequest.getPageSize());
                sweepVillageActivityQuery.setCurrentPage(sweepVillageActivityQueryRequest.getCurrentPage());
            }
            log.info("[getSweepVillageActivityList] 获取进行中的扫村活动列表,query:{}", sweepVillageActivityQuery);
            List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);
            //do转为vo
            List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
            for (SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS) {
                SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
                resultVO.setTitle(sweepVillageActivityDO.getTitle());
                resultVO.setStartTime(sweepVillageActivityDO.getStartTime());
                resultVO.setAddress(sweepVillageActivityDO.getAddress());
                resultVO.setArea(sweepVillageActivityDO.getArea());
                resultVO.setVillageId(sweepVillageActivityDO.getVillageId());
                resultVO.setVillageName(sweepVillageActivityDO.getVillageName());
                resultVO.setSweepVillageActivityId(sweepVillageActivityDO.getId());
                resultVO.setCreatorName(sweepVillageActivityDO.getCreatorName());
                resultVO.setCreateTime(sweepVillageActivityDO.getGmtCreate());
                resultVO.setStatus(sweepVillageActivityDO.getStatus());

                resultVOList.add(resultVO);
            }
            return ApiResult.of(0, ListVO.<SweepVillageActivityResultVO>builder().rows(resultVOList)
                    .totalCount((long) resultVOList.size())
                    .pageSize(pageSize)
                    .currentPage(currentPage)
                    .build());
        }

        if (sweepVillageActivityQueryRequest.getStatus().equals(SweepVillageStatusEnum.END.getId())) {

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

            sweepVillageActivityQuery.setPageEnable(false);
            if (sweepVillageActivityQueryRequest.getCurrentPage() != null) {
                sweepVillageActivityQuery.setPageEnable(true);
                sweepVillageActivityQuery.setPageSize(sweepVillageActivityQueryRequest.getPageSize());
                sweepVillageActivityQuery.setCurrentPage(sweepVillageActivityQueryRequest.getCurrentPage());
            }


            log.info("[getSweepVillageActivityList] 获取已结束的扫村活动列表,query:{}", sweepVillageActivityQuery);
            List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);
            //do 转化为vo
            List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
            for (SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS) {
                SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
                BeanUtils.copyProperties(sweepVillageActivityDO, resultVO);
                resultVO.setSweepVillageActivityId(sweepVillageActivityDO.getId());
                resultVO.setCreateTime(sweepVillageActivityDO.getGmtCreate());

                //获取统计量
                SweepVillageMarketingNumberQuery query = new SweepVillageMarketingNumberQuery();
                query.setActivityId(sweepVillageActivityDO.getId());
                query.setMobile(sweepVillageActivityDO.getMobile());
                SweepVillageMarketingNumberDO sweepVillageMarketingNumberDO = sweepVillageMarketingNumberMapper.get(query);
                if (sweepVillageMarketingNumberDO == null) {
                    log.error("[getSweepVillageActivityList] query market num is null,query:{}", query);
                    resultVO.setHandleCount(0);
                } else {
                    resultVO.setHandleCount(sweepVillageMarketingNumberDO.getCount());
                }

                //获取走访户数
                SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
                visitRecordingQuery.setActivityId(sweepVillageActivityDO.getId());
                long count = sweepVillageVisitRecordingMapper.count(visitRecordingQuery);
                resultVO.setVisitCount((int) count);
                resultVOList.add(resultVO);
            }
            return ApiResult.of(0, ListVO.<SweepVillageActivityResultVO>builder().rows(resultVOList)
                    .totalCount((long) resultVOList.size())
                    .currentPage(currentPage)
                    .pageSize(pageSize).build());
        }

        throw new ApiException("illegal status", 500);
    }

    @Override
    public ApiResult<SweepVillageActivityDetailVO> getSweepVillageActivityAndBizById(Long sweepVillageActivitiId) {
        //1.获取扫村活动信息
        SweepVillageActivityQuery activityQuery = new SweepVillageActivityQuery();
        activityQuery.setId(sweepVillageActivitiId);
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(activityQuery);
        if(sweepVillageActivityDO == null){
            log.error("[getSweepVillageActivityAndBizById] query activity is null,query:{}",activityQuery);
            return ApiResult.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST.msg,SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST.code);
        }

        //2.初始化返回对象
        SweepVillageActivityDetailVO activityDetailVO = new SweepVillageActivityDetailVO();
        activityDetailVO.setSweepVillageActivityId(sweepVillageActivityDO.getId());
        activityDetailVO.setTitle(sweepVillageActivityDO.getTitle());
        activityDetailVO.setVillageId(sweepVillageActivityDO.getVillageId());
        activityDetailVO.setVillageName(sweepVillageActivityDO.getVillageName());
        activityDetailVO.setAddress(sweepVillageActivityDO.getAddress());
        activityDetailVO.setCreateTime(sweepVillageActivityDO.getGmtCreate());
        activityDetailVO.setStartTime(sweepVillageActivityDO.getStartTime());
        activityDetailVO.setEndTime(sweepVillageActivityDO.getEndTime());
        activityDetailVO.setCreatorName(sweepVillageActivityDO.getCreatorName());

        activityDetailVO.setStatus(sweepVillageActivityDO.getStatus());
        if(sweepVillageActivityDO.getStatus() == SweepVillageStatusEnum.ABNORMAL_END.getId()){
            activityDetailVO.setExceptionMsg(SweepVillageStatusEnum.ABNORMAL_END.getDesc());
        }
        //2.查询业务办理信息
        SweepVillageMarketingNumberQuery marketingNumberQuery = new SweepVillageMarketingNumberQuery();
        marketingNumberQuery.setActivityId(sweepVillageActivitiId);

        SweepVillageMarketingNumberDO sweepVillageMarketingNumberDO = sweepVillageMarketingNumberMapper.get(marketingNumberQuery);
        if(sweepVillageMarketingNumberDO == null){
            log.error("[getSweepVillageActivityAndBizById] query market is null,query:{}",marketingNumberQuery);
            activityDetailVO.setBizList(new ArrayList<SweepVillageBizDetail>());
            return ApiResult.of(0,activityDetailVO);
        }
        Gson gson = new Gson();
        List<SweepVillageBizDetail> bizList = gson.fromJson(sweepVillageMarketingNumberDO.getDetail(),
                new TypeToken<List<SweepVillageBizDetail>>() {
                }.getType());
        activityDetailVO.setBizList(bizList);
        return ApiResult.of(0,activityDetailVO);

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


        SweepVillageActivityFinishVO sweepVillageActivityFinishVO = new SweepVillageActivityFinishVO();
        sweepVillageActivityFinishVO.setSweepVillageCount(sweepVillageActivityDOS.size());
        sweepVillageActivityFinishVO.setVisitUserCount(visitRecordingDOS.size());
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
                //设置count值
                List<SweepVillageBizDetail> bizList = request.getBizList();
                for (SweepVillageBizDetail detail : bizList) {
                    detail.setCount(detail.getNum());
                }
                newMarketingNumberDO.setDetail(GsonUtils.toJson(bizList));
            } else {
                newMarketingNumberDO.setCount(0);
                List<StallUpBizType> sweepVillageBizList = stallUpConfig.getConfig().getSweepVillageBizList();
                List<SweepVillageBizDetail> details = new ArrayList<>();
                for (StallUpBizType bizType : sweepVillageBizList) {
                    SweepVillageBizDetail bizDetail = new SweepVillageBizDetail();
                    bizDetail.setId(bizType.getId());
                    bizDetail.setName(bizType.getName());
                    bizDetail.setCount(0);
                    details.add(bizDetail);
                }
                newMarketingNumberDO.setDetail(GsonUtils.toJson(details));
            }
            newMarketingNumberDO.setRemark(request.getRemark());
            newMarketingNumberDO.setUserId(SmartGridContext.getUid());
            newMarketingNumberDO.setMobile(SmartGridContext.getMobile());
            sweepVillageMarketingNumberMapper.insert(newMarketingNumberDO);

            //同步给华为
            synchronizeSweepingData(newMarketingNumberDO);
        } else {
            //更新
            if (!CollectionUtils.isEmpty(request.getBizList())) {
                marketingNumberDO.setCount(request.getBizList().stream().mapToInt(v -> v.getNum()).sum());
                //设置count值
                List<SweepVillageBizDetail> bizList = request.getBizList();
                for (SweepVillageBizDetail detail : bizList) {
                    detail.setCount(detail.getNum());
                }
                marketingNumberDO.setDetail(GsonUtils.toJson(bizList));
            } else {
                marketingNumberDO.setCount(0);
                List<StallUpBizType> sweepVillageBizList = stallUpConfig.getConfig().getSweepVillageBizList();
                List<SweepVillageBizDetail> details = new ArrayList<>();
                for (StallUpBizType bizType : sweepVillageBizList) {
                    SweepVillageBizDetail bizDetail = new SweepVillageBizDetail();
                    bizDetail.setId(bizType.getId());
                    bizDetail.setName(bizType.getName());
                    bizDetail.setCount(0);
                    details.add(bizDetail);
                }
                marketingNumberDO.setDetail(GsonUtils.toJson(details));
            }
            marketingNumberDO.setRemark(request.getRemark());
            sweepVillageMarketingNumberMapper.update(marketingNumberDO);

            //同步给华为
            synchronizeSweepingData(marketingNumberDO);
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
                bizDetail.setCount(0);
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


        numberVO.setUsername(SmartGridContext.getUserName());
        return ApiResult.of(0, numberVO);
    }

    @Override
    public ApiResult<VillageVO> getLocationByVillageId(String id) {
        Assert.hasText(id, "id is null");
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setVillageId(id);
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            log.error("[getLocationByVillageId] 扫村活动不存在,villageId:{}", id);
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST);
        }
        VillageVO villageVO = new VillageVO();
        villageVO.setId(sweepVillageActivityDO.getVillageId());
        villageVO.setName(sweepVillageActivityDO.getVillageName());
        villageVO.setGridId(sweepVillageActivityDO.getGridId());
        villageVO.setArea(sweepVillageActivityDO.getArea());
        villageVO.setAreaCode(sweepVillageActivityDO.getAreaCode());
        villageVO.setLocation(sweepVillageActivityDO.getLocation());
        villageVO.setOriginLocation(sweepVillageActivityDO.getOriginLocation());
        return ApiResult.of(0, villageVO);
    }

    @Override
    public ApiResult<Void> fixDatabase() {
        //1.查询所有的扫村活动
        SweepVillageActivityQuery query = new SweepVillageActivityQuery();
        query.setPageEnable(false);
        List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(query);
        if(sweepVillageActivityDOS == null){
            log.error("[fixDatabase] activityList is null");
            return ApiResult.fail(500,"订正失败");
        }
        for (SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS) {
            //2.获取创建人名称
            UserOperateLogQuery logQuery = new UserOperateLogQuery();
            logQuery.setMobile(sweepVillageActivityDO.getMobile());

            logQuery.setTableIndex(SubTableUtils.getTableIndexByOnlyMonth(LocalDate.now()));
            UserOperateLogDO userOperateLogDO = userOperateLogMapper.get(logQuery);
            if(userOperateLogDO == null){
                log.error("[fixDatabase] creator is null,query:{}",query);
                continue;
            }
            SweepVillageActivityDO sweepVillageActivityDOUpdate = new SweepVillageActivityDO();
            sweepVillageActivityDOUpdate.setId(sweepVillageActivityDO.getId());
            sweepVillageActivityDOUpdate.setCreatorName(userOperateLogDO.getUsername());

            //3.更新
            sweepVillageActivityMapper.update(sweepVillageActivityDOUpdate);
        }
        log.info("[fixDatabase] 订正扫村数据成功");
        return ApiResult.of(0,null,"success");
    }

    @Override
    public ApiResult<ListVO<SweepVillageActivityResultVO>> getSweepVillageActivityListDetail(SweepVillageActivityQueryRequest request) {

        SweepVillageActivityQuery query = new SweepVillageActivityQuery();
        query.setPageEnable(false);
        query.setGridId(request.getGridId());
        if(request.getCurrentPage() != null && request.getPageSize() != null){
            query.setPageEnable(true);
            query.setCurrentPage(request.getCurrentPage());
            query.setPageSize(request.getPageSize());
        }

        if(request.getMobile() != null){
            query.setMobile(request.getMobile());
        }

        if(request.getStartTime() != null){
            query.setStartTime(request.getStartTime());
        }

        if(request.getEndTime() != null){
            query.setEndTime(request.getEndTime());
        }

        if(request.getStatus() != null){
            if(request.getStatus() == SweepVillageStatusEnum.END.getId()){
                query.setStatusList(Lists.newArrayList(
                        SweepVillageStatusEnum.END.getId(),
                        SweepVillageStatusEnum.ABNORMAL_END.getId()
                ));
            }else {
                query.setStatus(request.getStatus());
            }
        }

        log.info("[getSweepVillageActivityListDetail] find sweepVillageActivityList");
        List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(query);


        //do转为vo
        List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
        for (SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS) {
            SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
            resultVO.setTitle(sweepVillageActivityDO.getTitle());
            resultVO.setAddress(sweepVillageActivityDO.getAddress());
            resultVO.setArea(sweepVillageActivityDO.getArea());
            resultVO.setVillageId(sweepVillageActivityDO.getVillageId());
            resultVO.setVillageName(sweepVillageActivityDO.getVillageName());
            resultVO.setSweepVillageActivityId(sweepVillageActivityDO.getId());
            resultVO.setCreatorName(sweepVillageActivityDO.getCreatorName());
            resultVO.setCreateTime(sweepVillageActivityDO.getGmtCreate());
            resultVO.setStatus(sweepVillageActivityDO.getStatus());
            resultVO.setStartTime(sweepVillageActivityDO.getStartTime());
            resultVO.setEndTime(sweepVillageActivityDO.getEndTime());

            resultVOList.add(resultVO);
        }

        return ApiResult.of(0,ListVO.<SweepVillageActivityResultVO>builder()
                .rows(resultVOList)
                .pageSize(request.getPageSize())
                .currentPage(request.getCurrentPage())
                .totalCount((long)resultVOList.size()).build());
    }


    private ApiResult<SweepVillageActivityDO> checkActivityExist(Long activityId) {
        //判断扫村活动是否存在
        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(activityId);
        SweepVillageActivityDO activityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (activityDO == null) {
            log.error("[checkActivityExist] error,query:{}", sweepVillageActivityQuery);
            return null;
        }
        return ApiResult.of(0, activityDO);
    }


    /**
     * 同步给华为
     *
     * @param sweepVillageMarketingNumberDO
     */
    private void synchronizeSweepingData(SweepVillageMarketingNumberDO sweepVillageMarketingNumberDO) {
        Map<String, Object> map = new HashMap<>();
        Gson gson = new Gson();
        List<SweepVillageBizDetail> bizDetails = gson.fromJson(sweepVillageMarketingNumberDO.getDetail(),
                new TypeToken<List<SweepVillageBizDetail>>() {
                }.getType());

        map.put("activityId", SweepVillageConstants.ID_PREFIX + sweepVillageMarketingNumberDO.getActivityId());

        map.put("mobile", SmartGridContext.getMobile());
        map.put("bizInfoList", bizDetails);
        map.put("remark", sweepVillageMarketingNumberDO.getRemark());

        thirdApiMappingService.asyncDispatch(map, HuaweiSweepVillageUrlEnum.ADD_OR_UPDATE_VILLAGE_BIZ_DATA.getApiName(), SmartGridContext.getMobile());
    }

}


