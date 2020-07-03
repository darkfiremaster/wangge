package com.shinemo.wangge.web.controller.sweepvillage;

import cn.hutool.core.lang.Assert;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepfloor.domain.vo.SweepFloorBusinessCountAndHouseCountVO;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityFinishVO;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/6/5 11:38
 * @Desc
 */
@RestController
@RequestMapping("/sweepvillage")
public class SweepVillageActivityController {

    @Autowired
    private SweepVillageActivityService sweepVillageActivityService;

    @PostMapping("/createVillage")
    public ApiResult<Map<String, Object>> createVillage() {
        return sweepVillageActivityService.createVillage(null);
    }


    @PostMapping("/getActivityList")
    public ApiResult getActivityList(@RequestBody SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest) {
        Assert.notNull(sweepVillageActivityQueryRequest,"sweepVillageActivityQueryRequest is null");
        Assert.notNull(sweepVillageActivityQueryRequest.getStatus(),"status is null");
        return sweepVillageActivityService.getSweepVillageActivityList(sweepVillageActivityQueryRequest);
    }

    /**
     * 扫村次数 查询
     * @param type
     * @return
     */
    @GetMapping("/getFinishResultInfo")
    @SmIgnore
    public ApiResult<SweepVillageActivityFinishVO> getFinishResultInfo(@RequestParam Integer type) {
        org.springframework.util.Assert.notNull(type,"type is null");
        return sweepVillageActivityService.getFinishResultInfo(type);
    }
}
