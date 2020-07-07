package com.shinemo.wangge.web.controller.sweepvillage;

import cn.hutool.core.lang.Assert;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.domain.request.SweepVillageBusinessRequest;
import com.shinemo.sweepvillage.domain.vo.*;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/6/5 11:38
 * @Desc
 */
@Slf4j
@RestController
@RequestMapping("/sweepvillage")
public class SweepVillageActivityController {

    @Autowired
    private SweepVillageActivityService sweepVillageActivityService;
    @Resource
    private StallUpConfig stallUpConfig;

    /**
     * 获取用户上次扫村打卡的村庄
     */
    @PostMapping("/getUserLastVillage")
    public ApiResult<VillageVO> getUserLastVillage() {
        return sweepVillageActivityService.getUserLastVillage();
    }

    /**
     * 新建村庄
     */
    @PostMapping("/createVillage")
    public ApiResult<Map<String, Object>> createVillage(@RequestBody VillageVO villageVO) {
        return sweepVillageActivityService.createVillage(villageVO);
    }


    /**
     * 获取村庄列表
     */
    @PostMapping("/getVillageList")
    public ApiResult<Map<String, Object>> getVillageList() {
        return sweepVillageActivityService.getVillageList();
    }

    /**
     * 新建扫村活动
     */
    @PostMapping("/createSweepVillageActivity")
    public ApiResult<Void> createSweepVillageActivity(@RequestBody SweepVillageActivityVO sweepVillageActivityVO) {
        return sweepVillageActivityService.createSweepVillageActivity(sweepVillageActivityVO);
    }


    /**
     * 结束扫村
     */
    @PostMapping("/finishActivity")
    public ApiResult<Void> finishActivity(@RequestBody SweepVillageSignVO sweepVillageSignVO) {
        return sweepVillageActivityService.finishActivity(sweepVillageSignVO);
    }

    /**
     * 获取扫村活动列表
     */
    @GetMapping("/getSweepVillageActivityList")
    public ApiResult getSweepVillageActivityList(@RequestParam Integer status,
                                     @RequestParam String startTime,
                                     @RequestParam String endTime,
                                     @RequestParam(required = false)  Integer pageSize,
                                     @RequestParam(required = false) Integer currentPage) {
        Assert.notNull(status,"status is null");
        SweepVillageActivityQueryRequest request = new SweepVillageActivityQueryRequest();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        request.setStatus(status);
        try {
            request.setStartTime(format.parse(startTime));
            request.setEndTime(format.parse(endTime));
        } catch (ParseException e) {
            log.error("[getSweepVillageActivityList] date parse error,startTime:{},endTime:{}",startTime,endTime);
            return ApiResult.fail(SweepVillageErrorCodes.DATE_PARSE_ERROR.code,SweepVillageErrorCodes.DATE_PARSE_ERROR.msg);
        }
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return sweepVillageActivityService.getSweepVillageActivityList(request);
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


    /**
     * 查询扫楼业务办理按钮
     * @return
     */
    @GetMapping("/getSweepVillageBizList")
    @SmIgnore
    public ApiResult<SweepVillageBizListVO> getSweepVillageBizList() {

        //获取摆摊配置
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        SweepVillageBizListVO bizListVO = new SweepVillageBizListVO();

        //配置不存在，返回空
        if (config == null) {
            log.error("[getSweepVillageBizList]bizList empty");
            bizListVO.setSweepVillageList(new ArrayList<>());
            bizListVO.setSweepVillageBizList(new ArrayList<>());
            return ApiResult.of(0,bizListVO);
        }
        //获取 营销业务列表
        List<StallUpBizType> sweepVillageBizList = config.getSweepVillageBizList();
        if (!CollectionUtils.isEmpty(sweepVillageBizList)) {
            bizListVO.setSweepVillageBizList(sweepVillageBizList);
        }
        List<StallUpBizType> marketToolList = new ArrayList<>();
        List<StallUpBizType> sweepVillageList = new ArrayList<>();
        List<StallUpBizType> sweepVillageListConfig = config.getSweepVillageList();
        if (!CollectionUtils.isEmpty(sweepVillageListConfig)) {

            for (int i = 0;i < sweepVillageListConfig.size();i++) {
                StallUpBizType bizType = sweepVillageListConfig.get(i);
                if (bizType.getId().equals(19L)) {
                    marketToolList.add(bizType);
                }else {
                    sweepVillageList.add(bizType);
                }

            }
            bizListVO.setSweepVillageList(sweepVillageList);
            bizListVO.setMarketToolList(marketToolList);
        }
        return ApiResult.of(0,bizListVO);
    }


    /**
     * 办理量录入
     * @param request
     * @return
     */
    @PostMapping("/addBusiness")
    public ApiResult<Void> addOrUpdateBusiness(@RequestBody SweepVillageBusinessRequest request){
        org.springframework.util.Assert.notNull(request,"request is null");
        org.springframework.util.Assert.notNull(request.getActivityId(),"activityId is null");
        return sweepVillageActivityService.enterMarketingNumber(request);
    }


    /**
     * 办理量查询
     * @param activityId
     * @return
     */
    @GetMapping("/getVillageMarketNumber")
    @SmIgnore
    public ApiResult<SweepVillageBusinessRequest> getMarketingNumber(@RequestParam Long activityId) {
        org.springframework.util.Assert.notNull(activityId,"activityId is null");
        return sweepVillageActivityService.getMarketingNumber(activityId);
    }
}
