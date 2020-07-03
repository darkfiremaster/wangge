package com.shinemo.wangge.core.service.sweepvillage.impl;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.exception.CommonErrorCodes;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.sweepfloor.domain.model.SweepFloorMarketingNumberDO;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepvillage.domain.model.SweepVillageActivityDO;
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
            //查已结束的活动
            SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
            sweepVillageActivityQuery.setMobile(SmartGridContext.getMobile());
            sweepVillageActivityQuery.setStatusList(Lists.newArrayList(
                    SweepVillageStatusEnum.END.getId(),
                    SweepVillageStatusEnum.ABNORMAL_END.getId()
            ));
            sweepVillageActivityQuery.setStartTime(sweepVillageActivityQueryRequest.getStartTime());
            sweepVillageActivityQuery.setEndTime(sweepVillageActivityQueryRequest.getEndTime());

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

        return ApiResult.of(0, null);
    }

    @Override
    public ApiResult<SweepVillageActivityResultVO> getFinshResultInfo(SweepFloorActivityQuery sweepFloorActivityQuery) {
        //1.获取已结束
        return null;
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


