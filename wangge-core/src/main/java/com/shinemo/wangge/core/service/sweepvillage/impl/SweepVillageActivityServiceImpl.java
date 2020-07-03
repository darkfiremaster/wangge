package com.shinemo.wangge.core.service.sweepvillage.impl;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.shinemo.client.common.StatusEnum;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.exception.CommonErrorCodes;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.sweepfloor.domain.model.SweepFloorMarketingNumberDO;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepvillage.domain.model.SweepVillageActivityDO;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageMarketingNumberQuery;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.enums.SweepVillageStatusEnum;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.sweepvillage.domain.query.SweepVillageActivityQuery;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityFinishVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityResultVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityVO;
import com.shinemo.sweepvillage.domain.vo.VillageVO;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.SweepFloorVisitRecordingMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageMarketingNumberMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageVisitRecordingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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


    private static final Integer WEEK_TYPE = 1;
    private static final Integer MONTH_TYPE = 2;

    @Override
    public ApiResult<Void> createSweepVillageActivity(SweepVillageActivityVO sweepVillageActivityVO) {
        Assert.notNull(sweepVillageActivityVO, "sweepVillageActivityVO is null");
        //todo 校验参数

        //todo 判断用户是否已有进行中的扫村活动

        SweepVillageActivityDO sweepFloorActivityDO = getSweepVillageActivityDO(sweepVillageActivityVO);
        sweepVillageActivityMapper.insert(sweepFloorActivityDO);

        //todo 同步华为
        return ApiResult.of(0);
    }


    @Override
    public ApiResult<List<VillageVO>> getVillageList() {
        //todo 透传华为
        return null;
    }


    @Override
    public ApiResult<Map<String, Object>> createVillage(VillageVO villageVO) {
        //todo 透传华为

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "skh");
        ApiResult<Map<String, Object>> result = thirdApiMappingService.dispatch(map, "wahaha");

        return result;
    }




    @Override
    public ApiResult<Void> finishActivity(SweepVillageActivityFinishVO sweepVillageActivityFinishVO) {
        //todo 校验参数
        Assert.notNull(sweepVillageActivityFinishVO.getId(), "id is null");

        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(sweepVillageActivityFinishVO.getId());
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST);
        }
        if (!sweepVillageActivityDO.getStatus().equals(SweepVillageStatusEnum.PROCESSING.getId())) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_STATUS_ERROR);
        }
        //todo 判断打卡距离

        sweepVillageActivityDO.setEndTime(new Date());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.END.getId());
        sweepVillageActivityMapper.update(sweepVillageActivityDO);

        //todo 插入签到表

        //todo 透传华为
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
            if(sweepVillageActivityDOS == null){
                log.error("[getSweepVillageActivityList] find error,query:{}",sweepVillageActivityQuery);
                return ApiResult.fail( CommonErrorCodes.SERVER_ERROR.code);
            }
            //do转为vo
            List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
            for(SweepVillageActivityDO  sweepVillageActivityDO : sweepVillageActivityDOS){
                SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
                resultVO.setStartTime(sweepVillageActivityDO.getStartTime());
                resultVO.setArea(sweepVillageActivityDO.getArea());
                resultVO.setVillageId(sweepVillageActivityDO.getVillageId());
                resultVO.setVillageName(sweepVillageActivityDO.getVillageName());
                resultVO.setSweepVillageActivityId(sweepVillageActivityDO.getId());
                resultVOList.add(resultVO);
            }
            return ApiResult.of(0,resultVOList);
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
            sweepVillageActivityQuery.putOrderBy("end_time",false);

            log.info("[getSweepVillageActivityList] 获取已结束的扫村活动列表,query:{}", sweepVillageActivityQuery);
            List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);
            //do 转化为vo
            List<SweepVillageActivityResultVO> resultVOList = new ArrayList<>();
            for(SweepVillageActivityDO sweepVillageActivityDO : sweepVillageActivityDOS){
                SweepVillageActivityResultVO resultVO = new SweepVillageActivityResultVO();
                BeanUtils.copyProperties(sweepVillageActivityDO,resultVO);

                //获取统计量
                SweepVillageMarketingNumberQuery query = new SweepVillageMarketingNumberQuery();
                query.setActivityId(sweepVillageActivityDO.getId());
                query.setMobile(sweepVillageActivityDO.getMobile());
                SweepFloorMarketingNumberDO sweepFloorMarketingNumberDO = sweepVillageMarketingNumberMapper.get(query);
                if(sweepFloorMarketingNumberDO == null){
                    log.error("[getSweepVillageActivityList] query market num error,query:{}",query);
                    continue;
                }

                resultVO.setHandleCount(sweepFloorMarketingNumberDO.getCount());

                //获取走访户数
                SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
                visitRecordingQuery.setActivityId(sweepVillageActivityDO.getId());
                long count = sweepVillageVisitRecordingMapper.count(visitRecordingQuery);
                resultVO.setVisitCount((int) count);
                resultVOList.add(resultVO);
            }
            return ApiResult.of(0,resultVOList);
        }

        throw new ApiException("illegal status",500);
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
        sweepVillageActivityDOS.forEach(val->{
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
        for(SweepVillageVisitRecordingDO visitRecordingDO : visitRecordingDOS){
            if(activityIdSet.contains(visitRecordingDO.getActivityId())){
                visitCount++;
            }

        }

        SweepVillageActivityFinishVO sweepVillageActivityFinishVO = new SweepVillageActivityFinishVO();
        sweepVillageActivityFinishVO.setSweepVillageCount(sweepVillageActivityDOS.size());
        sweepVillageActivityFinishVO.setVisitUserCount(visitCount);
        return ApiResult.of(0,sweepVillageActivityFinishVO);
    }

    private SweepVillageActivityDO getSweepVillageActivityDO(SweepVillageActivityVO sweepVillageActivityVO) {
        SweepVillageActivityDO sweepVillageActivityDO = new SweepVillageActivityDO();
        sweepVillageActivityDO.setTitle(sweepVillageActivityVO.getTitle());
        sweepVillageActivityDO.setVillageId(sweepVillageActivityVO.getVillageId());
        sweepVillageActivityDO.setVillageName(sweepVillageActivityVO.getVillageName());
        sweepVillageActivityDO.setArea(sweepVillageActivityVO.getArea());
        sweepVillageActivityDO.setAreaCode(sweepVillageActivityVO.getAreaCode());
        sweepVillageActivityDO.setLocation(sweepVillageActivityVO.getLocation());
        sweepVillageActivityDO.setRgsLocation(sweepVillageActivityVO.getRgsLocation());
        sweepVillageActivityDO.setMobile(SmartGridContext.getMobile());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.NOT_START.getId());

        //TODO
        return null;
    }
}


