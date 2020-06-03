package com.shinemo.wangge.core.service.sweepvillage.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepfloor.domain.vo.SweepFloorActivityVO;
import com.shinemo.sweepvillage.domain.SweepVillageActivityDO;
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
        //todo

        SweepVillageActivityDO sweepFloorActivityDO = getSweepVillageActivityDO(sweepVillageActivityVO);
        sweepVillageActivityMapper.insert(sweepFloorActivityDO);

        return ApiResult.of(0);
    }

    private SweepVillageActivityDO getSweepVillageActivityDO(SweepVillageActivityVO sweepVillageActivityVO) {
        return null;
    }

    @Override
    public ApiResult<List<VillageVO>> getVillageList(VillageQuery villageQuery) {
        return null;
    }

    @Override
    public ApiResult<Void> createVillage(VillageVO villageVO) {
        return null;
    }

    @Override
    public ApiResult<Void> startActivity(Long id) {
        return null;
    }


    @Override
    public ApiResult<Void> finishActivity(SweepVillageActivityFinishVO sweepVillageActivityFinishVO) {
        return null;
    }

    @Override
    public ApiResult<List<SweepFloorActivityVO>> getSweepVillageActivityList(SweepFloorActivityQuery sweepFloorActivityQuery) {
        return null;
    }

    @Override
    public ApiResult<SweepVillageActivityResultVO> getFinshResultInfo(SweepFloorActivityQuery sweepFloorActivityQuery) {
        return null;
    }
}


