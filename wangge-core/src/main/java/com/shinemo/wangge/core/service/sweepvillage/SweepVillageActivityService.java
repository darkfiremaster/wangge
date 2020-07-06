package com.shinemo.wangge.core.service.sweepvillage;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.domain.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:32
 * @Desc 扫村
 */
public interface SweepVillageActivityService {

    /**
     * 新建村庄
     * @param villageVO
     * @return
     */
    ApiResult<Map<String, Object>> createVillage(VillageVO villageVO);


    /**
     * 获取村庄列表
     * @return
     */
    ApiResult<Map<String, Object>> getVillageList();


    /**
     * 新建扫村活动
     * @param sweepVillageActivityVO
     * @return
     */
    ApiResult<Void> createSweepVillageActivity(SweepVillageActivityVO sweepVillageActivityVO);

    /**
     * 扫村结束打卡
     * @return
     */
    ApiResult<Void> finishActivity(SweepVillageSignVO sweepVillageSignVO);


    /**
     * 获取扫村活动列表
     * @return
     */
    ApiResult<List<SweepVillageActivityResultVO>> getSweepVillageActivityList(SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest);

    /**
     * 获取扫村完成情况
     * @param type 1-周记录 2-月记录
     * @return
     */
    ApiResult<SweepVillageActivityFinishVO> getFinishResultInfo(Integer type);

}
