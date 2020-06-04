package com.shinemo.wangge.core.service.sweepvillage.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepvillage.domain.SweepVillageActivityDO;
import com.shinemo.sweepvillage.enums.SweepVillageStatusEnum;
import com.shinemo.sweepvillage.query.SweepVillageActivityQuery;
import com.shinemo.sweepvillage.query.VillageQuery;
import com.shinemo.sweepvillage.vo.SweepVillageActivityFinishVO;
import com.shinemo.sweepvillage.vo.SweepVillageActivityResultVO;
import com.shinemo.sweepvillage.vo.SweepVillageActivityVO;
import com.shinemo.sweepvillage.vo.VillageVO;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:32
 * @Desc
 */
@Service
@Slf4j
public class SweepVillageActivityServiceImpl implements SweepVillageActivityService {

    @Autowired
    private SweepVillageActivityMapper sweepVillageActivityMapper;

    @Override
    public ApiResult<Void> createSweepVillageActivity(SweepVillageActivityVO sweepVillageActivityVO) {
        Assert.notNull(sweepVillageActivityVO, "sweepVillageActivityVO is null");
        //todo 校验参数

        SweepVillageActivityDO sweepFloorActivityDO = getSweepVillageActivityDO(sweepVillageActivityVO);
        sweepVillageActivityMapper.insert(sweepFloorActivityDO);

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
    public ApiResult<Void> createVillage(VillageVO villageVO) {
        //todo 透传华为
        return null;
    }

    @Override
    public ApiResult<Void> startActivity(Long id) {
        //todo 校验参数

        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(id);
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            //todo
            return ApiResult.fail("数据不存在", 500);
        }

        sweepVillageActivityDO.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
        sweepVillageActivityMapper.update(sweepVillageActivityDO);

        //todo 透传华为
        //todo 同步代办
        return ApiResult.of(0);
    }


    @Override
    public ApiResult<Void> finishActivity(SweepVillageActivityFinishVO sweepVillageActivityFinishVO) {
        //todo 校验参数

        SweepVillageActivityQuery sweepVillageActivityQuery = new SweepVillageActivityQuery();
        sweepVillageActivityQuery.setId(sweepVillageActivityFinishVO.getId());
        SweepVillageActivityDO sweepVillageActivityDO = sweepVillageActivityMapper.get(sweepVillageActivityQuery);
        if (sweepVillageActivityDO == null) {
            //todo
            return ApiResult.fail("数据不存在", 500);
        }

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
        return null;
    }

    private SweepVillageActivityDO getSweepVillageActivityDO(SweepVillageActivityVO sweepVillageActivityVO) {
        return null;
    }
}


