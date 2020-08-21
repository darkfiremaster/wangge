package com.shinemo.wangge.web.controller.sweepstreet;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepstreet.domain.request.*;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetBusinessIndexVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMarketNumberVO;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetMarketService;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/sweepStreet")
class SweepStreetActivityController {


    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private SweepStreetService sweepStreetService;

    @Resource
    private SweepStreetMarketService sweepStreetMarketService;

    /**
     * 集团服务日业务查询首页
     *
     * @return
     */
    @GetMapping("/sweepStreetActivityBizDetail")
    public ApiResult<SweepStreetBusinessIndexVO> getSweepStreetBizDetail() {
        //获取配置
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        SweepStreetBusinessIndexVO sweepStreetBusinessIndexVO = new SweepStreetBusinessIndexVO();

        //配置不存在，返回空
        if (config == null) {
            log.error("[getSweepStreetBizDetail] bizList empty");
            sweepStreetBusinessIndexVO.setMarketToolList(new ArrayList<>());
            sweepStreetBusinessIndexVO.setBizMarketBizList(new ArrayList<>());
            sweepStreetBusinessIndexVO.setBizMarketBizDataList(new ArrayList<>());

            return ApiResult.of(0, sweepStreetBusinessIndexVO);
        }
        //获取 营销业务列表
        sweepStreetBusinessIndexVO.
                setMarketToolList(config.getSweepStreetToolList() == null ? new ArrayList<>() : config.getSweepStreetToolList());
        sweepStreetBusinessIndexVO.
                setBizMarketBizList(config.getSweepStreetBizList() == null ? new ArrayList<>() : config.getSweepStreetBizList());
        sweepStreetBusinessIndexVO.
                setBizMarketBizDataList(config.getSweepStreetBizDataList() == null ? new ArrayList<>() : config.getSweepStreetBizDataList());


        return ApiResult.of(0, sweepStreetBusinessIndexVO);
    }


    /**
     * 创建扫街活动
     *
     * @param request
     * @return
     */
    @PostMapping("/createSweepStreetActivity")
    public ApiResult createSweepStreetActivity(@RequestBody SweepStreetActivityRequest request) {
        Assert.notNull(request, "request is null");
        //创建扫街活动
        sweepStreetService.createSweepStreet(request);
        return ApiResult.success();
    }

    /**
     * 业务列表查询接口
     *
     * @param activityId
     * @return
     */
    @GetMapping("/getSweepStreetMarketNumber")
    public ApiResult<SweepStreetMarketNumberVO> getSweepStreetBizDetail(@RequestParam Long activityId) {
        return sweepStreetMarketService.getByActivityId(activityId);
    }

    /**
     * 业务列表录入接口
     *
     * @param request
     * @return
     */
    @PostMapping("/saveStreetScanBiz")
    public ApiResult addBusiness(@RequestBody SweepStreetBusinessRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getActivityId(), "sweepStreet activityId is null");
        Assert.notEmpty(request.getBizList(), "sweep street bizList is empty");

        return sweepStreetMarketService.updateMarketingNumber(request);
    }

    @PostMapping("/syncSweepStreetActivityBusi")
    public ApiResult syncSweepStreetActivityBusi(@RequestBody SweepStreetBusinessRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getActivityId(), "sweepStreet activityId is null");

        return sweepStreetMarketService.syncSweepStreetActivityBusi(request.getActivityId());
    }


    /**
     * 掃街列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getSweepStreetList")
    public ApiResult<ListVO<SweepStreetActivityVO>> getSweepStreetList(SweepStreetListRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getStatus(), "status is null");
        return sweepStreetService.getSweepStreetList(request);
    }

    /**
     * 開始打卡
     *
     * @param request
     * @return
     */
    @PostMapping("/startSign")
    public ApiResult startSign(@RequestBody SweepStreetSignRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getActivityId(), "activityId is null");
        Assert.notNull(request.getLocationDetailVO(), "locationDetailVO is null");
        return sweepStreetService.startSign(request);
    }

    /**
     * 结束打卡
     *
     * @param request
     * @return
     */
    @PostMapping("/endSign")
    public ApiResult endSign(@RequestBody SweepStreetSignRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getActivityId(), "activityId is null");
        Assert.notNull(request.getLocationDetailVO(), "locationDetailVO is null");
        return sweepStreetService.endSign(request);
    }


    /**
     * 周，月办理量
     *
     * @param type
     * @return
     */
    @GetMapping("/getFinishedCount")
    public ApiResult getFinishedCount(Integer type) {

        return sweepStreetService.getFinishedCount(type);
    }


    @GetMapping("/findMerchantList")
    public ApiResult findMerchantList(@RequestParam(required = false) String queryParam,
                                      @RequestParam String location,
                                      @RequestParam Integer pageSize,
                                      @RequestParam Integer currentPage) {
        return sweepStreetService.getMerchantList(HuaweiMerchantRequest.builder()
                .queryParam(queryParam)
                .location(location)
                .pageSize(pageSize)
                .currentPage(currentPage)
                .build());
    }

//    @GetMapping("/findMerchantListWithMap")
//    public ApiResult findMerchantListWithMap(@RequestParam(required = false) String queryParam,
//                                      @RequestParam String location,
//                                      @RequestParam Integer pageSize,
//                                      @RequestParam Integer currentPage){
//        return sweepStreetService.getMerchantList(HuaweiMerchantRequest.builder()
//                .queryParam(queryParam)
//                .location(location)
//                .pageSize(pageSize)
//                .currentPage(currentPage)
//                .radius(SweepStreetActivityConstants.DEFAULT_DISTANCE)
//                .build());
//    }

    /**
     * 取消活动
     *
     * @return
     */
    @PostMapping("/cancel")
    public ApiResult cancel(@RequestParam Long id) {
        Assert.notNull(id, "id is null");
        return sweepStreetService.cancel(id);
    }

    /**
     * 获取跳转商户详情url
     */
    @GetMapping("/getBusinessDetailUrl")
    public ApiResult<String> getBusinessDetailUrl(String groupId,String location) {
        Assert.notNull(groupId, "groupId is null");
        Assert.notNull(location, "location is null");
        return sweepStreetService.getBusinessDetailUrl(groupId,location);
    }

}
