package com.shinemo.wangge.web.controller.sweepstreet;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupDO;
import com.shinemo.groupserviceday.domain.request.*;
import com.shinemo.groupserviceday.domain.vo.*;
import com.shinemo.sweepstreet.domain.request.SweepStreetBusinessRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetListRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetSignRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetBusinessIndexVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMarketNumberVO;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.groupserviceday.GroupSerDayRedirctService;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayMarketingNumberService;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sweepStreet")
class SweepStreetActivityController {



    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private SweepStreetService sweepStreetService;

    /**
     * 集团服务日业务查询首页
     * @return
     */
    @GetMapping("/getSweepStreetBizDetail")
    public ApiResult<SweepStreetBusinessIndexVO> getSweepStreetBizDetail() {

        return ApiResult.of(0, null);
    }


    /**
     * 业务列表查询接口
     * @param activityId
     * @return
     */
    @GetMapping("/getSweepStreetMarketNumber")
    public ApiResult<SweepStreetMarketNumberVO> getSweepStreetBizDetail(@RequestParam Long activityId) {
        return null;
    }
    /**
     * 业务列表录入接口
     * @param request
     * @return
     */
    @PostMapping("/saveSweepStreetBusiness")
    public ApiResult addBusiness(@RequestBody SweepStreetBusinessRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"sweepStreet activityId is null");
        return null;
    }


    /**
     * 掃街列表
     * @param request
     * @return
     */
    @GetMapping("/getSweepStreetList")
    public ApiResult<ListVO<SweepStreetActivityVO>> getSweepStreetList(SweepStreetListRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getStatus(),"status is null");
        return sweepStreetService.getSweepStreetList(request);
    }

    /**
     * 開始打卡
     * @param request
     * @return
     */
    @PostMapping("/startSign")
    public ApiResult startSign(@RequestBody SweepStreetSignRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getLocationDetailVO(),"locationDetailVO is null");
        return sweepStreetService.startSign(request);
    }

}
