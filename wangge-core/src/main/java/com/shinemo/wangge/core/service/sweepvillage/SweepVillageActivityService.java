package com.shinemo.wangge.core.service.sweepvillage;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityFinishVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityResultVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityVO;
import com.shinemo.sweepvillage.domain.vo.VillageVO;

import java.util.List;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:32
 * @Desc 扫村
 */
public interface SweepVillageActivityService {


    /**
     * 获取村庄列表
     * @return
     */
    ApiResult<List<VillageVO>> getVillageList();


    /**
     * 新建村庄
     * @param villageVO
     * @return
     */
    ApiResult<Map<String, Object>> createVillage(VillageVO villageVO);


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
    ApiResult<Void> finishActivity(SweepVillageActivityFinishVO sweepVillageActivityFinishVO);


    /**
     * 获取扫村活动列表
     * @return
     */
    ApiResult<List<SweepVillageActivityResultVO>> getSweepVillageActivityList(SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest);

    /**
     * 获取扫村完成情况
     * @param sweepFloorActivityQuery
     * @return
     */
    ApiResult<SweepVillageActivityResultVO> getFinshResultInfo(SweepFloorActivityQuery sweepFloorActivityQuery);

    /**
     * 历史扫村详情
     */


}
