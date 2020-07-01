package com.shinemo.wangge.core.service.sweepvillage.impl;

import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepvillage.domain.SweepVillageActivityDO;
import com.shinemo.sweepvillage.enums.SweepVillageStatusEnum;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.sweepvillage.query.SweepVillageActivityQuery;
import com.shinemo.sweepvillage.query.VillageQuery;
import com.shinemo.sweepvillage.vo.SweepVillageActivityFinishVO;
import com.shinemo.sweepvillage.vo.SweepVillageActivityResultVO;
import com.shinemo.sweepvillage.vo.SweepVillageActivityVO;
import com.shinemo.sweepvillage.vo.VillageVO;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public ApiResult<Void> createSweepVillageActivity(SweepVillageActivityVO sweepVillageActivityVO) {
        Assert.notNull(sweepVillageActivityVO, "sweepVillageActivityVO is null");
        //todo 校验参数

        SweepVillageActivityDO sweepFloorActivityDO = getSweepVillageActivityDO(sweepVillageActivityVO);
        sweepVillageActivityMapper.insert(sweepFloorActivityDO);

        //todo 同步活动网格关联表

        //todo 同步华为
        //todo 同步代办
        return ApiResult.of(0);
    }


    @Override
    public ApiResult<List<VillageVO>> getVillageList(VillageQuery villageQuery) {
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
    public ApiResult<Void> startActivity(Long id) {
        //校验参数
        Assert.notNull(id, "id is null");

        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(id);
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_ACTIVITY_NOT_EXIST);
        }
        if (!sweepVillageActivityDO.getStatus().equals(SweepVillageStatusEnum.NOT_START.getId())) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.SWEEP_VILLAGE_STATUS_ERROR);
        }

        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
        sweepVillageActivityDO.setStartTime(new Date());
        sweepVillageActivityMapper.update(sweepVillageActivityDO);

        //todo 透传华为
        return ApiResult.of(0);
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

        sweepVillageActivityDO.setEndTime(new Date());
        sweepVillageActivityDO.setRemark(sweepVillageActivityFinishVO.getRemark());
        sweepVillageActivityDO.setPicUrl(sweepVillageActivityFinishVO.getPicUrl());
        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.END.getId());
        sweepVillageActivityMapper.update(sweepVillageActivityDO);

        //todo 透传华为
        //todo 同步代办
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<List<SweepVillageActivityVO>> getSweepVillageActivityList(SweepVillageActivityQuery sweepVillageActivityQuery) {
        //todo 校验参数

        List<SweepVillageActivityDO> sweepVillageActivityDOS = sweepVillageActivityMapper.find(sweepVillageActivityQuery);

        //todo 转化为vo

        return ApiResult.of(0, null);
    }

    @Override
    public ApiResult<SweepVillageActivityResultVO> getFinshResultInfo(SweepFloorActivityQuery sweepFloorActivityQuery) {
        //todo 等产品确认
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


