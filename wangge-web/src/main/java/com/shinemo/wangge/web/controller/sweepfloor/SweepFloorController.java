package com.shinemo.wangge.web.controller.sweepfloor;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.AESUtil;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepfloor.domain.query.SweepFloorBuildingQuery;
import com.shinemo.sweepfloor.domain.query.UpdateSweepFloorStatusQuery;
import com.shinemo.sweepfloor.domain.vo.*;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.sweepfloor.SweepFloorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/sweepFloor")
public class SweepFloorController {

    @Resource
    private SweepFloorService sweepFloorService;
    @Resource
    private StallUpConfig stallUpConfig;

    @Value("${smartgrid.huawei.aesKey}")
    public String aeskey;

    /**
     * 创建扫楼活动
     * @param request
     * @return
     */
    @PostMapping("/createSweepFloor")
    @SmIgnore
    public ApiResult<Long> create(@RequestBody SweepFloorActivityVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        Assert.notNull(request.getCommunityName(),"communityName is null");
        Assert.notNull(request.getLocation(),"location is null");
        Assert.notNull(SmartGridContext.getMobile(),"mobile is null");
        return sweepFloorService.create(request);
    }

    /**
     * 更新扫楼活动状态
     * @param request
     * @return
     */
    @PostMapping("/updateSweepFloorStatus")
    @SmIgnore
    public ApiResult<Void> updateSweepFloorStatus(@RequestBody UpdateSweepFloorStatusQuery request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getId(),"id is null");
        Assert.notNull(request.getStatus(),"status is null");
        return sweepFloorService.updateSweepFloorStatus(request);
    }


    /**
     * 查询业务类型列表
     * @return
     */
    @GetMapping("/queryBizType")
    @SmIgnore
    public ApiResult<SweepFloorBizListVO> queryBizType() {
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        SweepFloorBizListVO bizListVO = new SweepFloorBizListVO();
        if (config == null) {
            log.error("[queryBizType]bizList empty");
            bizListVO.setSweepFloorBizList(new ArrayList<>());
            return ApiResult.of(0,bizListVO);
        }
        List<StallUpBizType> sweepFloorBizList = config.getSweepFloorBizList();
        if (!CollectionUtils.isEmpty(sweepFloorBizList)) {
            List<StallUpBizType> bizTypeList = new ArrayList<>();
            for (StallUpBizType stallUpBizType:sweepFloorBizList) {
                if (stallUpBizType.getIsDisplay() && !stallUpBizType.getType().equals(0)) {
                    bizTypeList.add(stallUpBizType);
                }
            }
            bizListVO.setSweepFloorBizList(bizTypeList);
        }else {
            bizListVO.setSweepFloorBizList(new ArrayList<>());
        }
        return ApiResult.of(0,bizListVO);
    }

    /**
     * 扫楼数、成功业务量查询
     * @param type
     * @return
     */
    @GetMapping("/getBusinessCountAndHouseCount")
    @SmIgnore
    public ApiResult<SweepFloorBusinessCountAndHouseCountVO> getBusinessCountAndHouseCount(@RequestParam Integer type) {
        Assert.notNull(type,"type is null");
        return sweepFloorService.getBusinessCountAndHouseCount(type);
    }

    /**
     * 办理量录入
     * @param request
     * @return
     */
    @PostMapping("/enterMarketingNumber")
    @SmIgnore
    public ApiResult<Void> enterMarketingNumber(@RequestBody SweepFloorMarketingNumberVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
//        if (CollectionUtils.isEmpty(request.getBizDetails())) {
//            return ApiResult.of(0);
//        }
        return sweepFloorService.enterMarketingNumber(request);
    }

    /**
     * 办理量查询
     * @param activityId
     * @return
     */
    @GetMapping("/getMarketingNumber")
    @SmIgnore
    public ApiResult<SweepFloorMarketingNumberVO> getMarketingNumber(@RequestParam Long activityId) {
        Assert.notNull(activityId,"activityId is null");
        return sweepFloorService.getMarketingNumber(activityId);
    }

    /**
     * 走访记录录入
     * @param request
     * @return
     */
    @PostMapping("/enterVisitRecording")
    @SmIgnore
    public ApiResult<Void> enterVisitRecording(@RequestBody SweepFloorVisitRecordingVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        Assert.notNull(request.getBuildingId(),"buildingId is null");
        Assert.notNull(request.getBuildingName(),"buildingName is null");
        Assert.notNull(request.getUnitId(),"unitId is null");
        Assert.notNull(request.getUnitName(),"unitName is null");
        Assert.notNull(request.getHouseNumber(),"houseNumber is null");
        Assert.notNull(request.getHouseId(),"houseId is null");
        Assert.notNull(request.getBusinessType(),"businessType is null");
        Assert.notNull(request.getComplaintSensitiveCustomersFlag(),"complaintSensitiveCustomersFlag is null");
        Assert.notNull(request.getSuccessFlag(),"successFlag is null");
        Assert.notNull(request.getBroadbandExpireDate(),"broadbandExpireDate is null");
        return sweepFloorService.enterVisitRecording(request);
    }

    /**
     * 走访记录查询
     * @param houseId
     * @return
     */
    @GetMapping("/getVisitRecording")
    @SmIgnore
    public ApiResult<List<SweepFloorVisitRecordingVO>> getVisitRecording(@RequestParam String houseId, @RequestParam Long activityId) {
        Assert.notNull(houseId,"houseId is null");
        Assert.notNull(activityId,"activityId is null");
        return sweepFloorService.getVisitRecording(houseId,activityId);
    }

    /**
     * 根据活动id走访记录查询
     * @param activityId
     * @return
     */
    @GetMapping("/getVisitRecordingByActivityId")
    @SmIgnore
    public ApiResult<List<SweepFloorVisitRecordingVO>> getVisitRecordingByActivityId(@RequestParam Long activityId) {
        Assert.notNull(activityId,"activityId is null");
        return sweepFloorService.getVisitRecordingByActivityId(activityId);
    }



    /**
     * 开始打卡
     * @param request
     * @return
     */
    @PostMapping("/startSign")
    @SmIgnore
    public ApiResult<Void> startSign(@RequestBody SignRecordQuery request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getLocationDetailVO(),"locationDetailVO is null");
        return sweepFloorService.satrtSign(request);
    }

    /**
     * 结束打卡
     * @param request
     * @return
     */
    @PostMapping("/endSign")
    @SmIgnore
    public ApiResult<Void> endSign(@RequestBody SignRecordQuery request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getImgUrl(),"imgUrl is null");
        Assert.notNull(request.getLocationDetailVO(),"locationDetailVO is null");
        return sweepFloorService.endSign(request);
    }

    /**
     * 扫楼活动查询
     * @return
     */
    @GetMapping("/getSweepFloorActivity")
    @SmIgnore
    public ApiResult<ListVO<SweepFloorActivityVO>> getSweepFloorActivity(@RequestParam Integer status, @RequestParam(required = false) Integer pageSize,
                                                                         @RequestParam(required = false) Integer currentPage,
                                                                         @RequestParam(required = false)Long startTime, @RequestParam(required = false)Long endTime) {
        return sweepFloorService.getSweepFloorActivity(status,pageSize,currentPage,startTime,endTime);
    }

    /**
     * 场外营销活动列表查询
     * @return
     */
    @GetMapping("/getOutsideActivityList")
    @SmIgnore
    public ApiResult<ListVO<SweepFloorActivityVO>> getOutsideActivityList(@RequestParam(required = false) Integer status,
                                                                          @RequestParam(required = false) Integer pageSize,
                                                                          @RequestParam(required = false) Integer currentPage,
                                                                          @RequestParam(required = false)Long startTime,
                                                                          @RequestParam(required = false)Long endTime,
                                                                          @RequestParam(required = false)String seMobile,
                                                                          @RequestParam String gridId) {
        Assert.notNull(gridId,"gridId is null");
        String mobile = null;
        if (!StringUtils.isBlank(seMobile)) {
            mobile = AESUtil.decrypt(seMobile, aeskey);
        }
        return sweepFloorService.getOutsideActivityList(status,pageSize,currentPage,startTime,endTime,mobile,gridId);
    }

    /**
     * 场外营销活动列表查询
     * @return
     */
    @GetMapping("/getOutsideActivityDetail")
    @SmIgnore
    public ApiResult<SweepFloorActivityVO> getOutsideActivityDetail(@RequestParam Long id) {
        Assert.notNull(id,"id is null");
        return sweepFloorService.getOutsideActivityDetail(id);
    }

    /**
     * 楼栋分布查询
     * @param request
     * @return
     */
    @GetMapping("/getBuildings")
    @SmIgnore
    public ApiResult<List<BuildingVO>> getBuildings(SweepFloorBuildingQuery request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        return sweepFloorService.getBuildings(request);
    }

    /**
     * 添加楼栋
     * @param request
     * @return
     */
    @PostMapping("/addBuilding")
    @SmIgnore
    public ApiResult<Void> addBuilding(@RequestBody BuildingVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        Assert.notNull(request.getBuildingName(),"buildingName is null");
        Assert.notNull(request.getUnitCount(),"unitCount is null");
        Assert.notNull(request.getFloorNumber(),"floorNumber is null");
        Assert.notNull(request.getHouseholderCountEveryFloor(),"householderCountEveryFloor is null");
        return sweepFloorService.addBuilding(request);
    }

    /**
     * 编辑楼栋
     * @param request
     * @return
     */
    @PostMapping("/updateBuilding")
    @SmIgnore
    public ApiResult<Void> updateBuilding(@RequestBody BuildingVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        Assert.notNull(request.getBuildingName(),"buildingName is null");
        Assert.notNull(request.getUnitCount(),"unitCount is null");
        Assert.notNull(request.getFloorNumber(),"floorNumber is null");
        Assert.notNull(request.getHouseholderCountEveryFloor(),"householderCountEveryFloor is null");
        Assert.notNull(request.getBuildingId(),"buildingId is null");
        return sweepFloorService.updateBuilding(request);
    }

    /**
     * 添加住户
     * @param request
     * @return
     */
    @PostMapping("/addHousehold")
    @SmIgnore
    public ApiResult<Void> addHousehold(@RequestBody HouseholdVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        Assert.notNull(request.getBuildingId(),"buildingId is null");
        Assert.notNull(request.getBuildingName(),"buildingName is null");
        Assert.notNull(request.getUnitId(),"unitId is null");
        Assert.notNull(request.getHouseNumber(),"houseNumber is null");
        Assert.notNull(request.getBroadbandFlag(),"broadbandFlag is null");
        return sweepFloorService.addHousehold(request);
    }

    /**
     * 编辑住户
     * @param request
     * @return
     */
    @PostMapping("/updateHousehold")
    @SmIgnore
    public ApiResult<Void> updateHousehold(@RequestBody HouseholdVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getHouseId(),"houseId is null");
        Assert.notNull(request.getCommunityId(),"communityId is null");
        Assert.notNull(request.getBuildingId(),"buildingId is null");
        Assert.notNull(request.getBuildingName(),"buildingName is null");
        Assert.notNull(request.getUnitId(),"unitId is null");
        Assert.notNull(request.getHouseNumber(),"houseNumber is null");
        Assert.notNull(request.getBroadbandFlag(),"broadbandFlag is null");
        return sweepFloorService.updateHousehold(request);
    }

    /**
     * 查询楼栋平面图、树形地址选择
     * @param request
     * @return
     */
    @GetMapping("/getBuildingPlan")
    @SmIgnore
    public ApiResult<List<BuildingDetailsVO>> getBuildingPlan(SweepFloorBuildingQuery request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getType(),"type is null");
        return sweepFloorService.getBuildingPlan(request);
    }

    /**
     * 查询楼栋住户信息
     * @param request
     * @return
     */
    @GetMapping("/getBuildingHouseholds")
    @SmIgnore
    public ApiResult<List<HouseholdVO>> getBuildingHouseholds(SweepFloorBuildingQuery request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getBuildingId(),"buildingId is null");
        return sweepFloorService.getBuildingHouseholds(request);
    }

    /**
     * 查询楼栋住户信息
     * @return
     */
    @GetMapping("/getFamilyMembers")
    @SmIgnore
    public ApiResult<List<FamilyMember>> getFamilyMembers(@RequestParam String buildingId,
                                                          @RequestParam String unitId, @RequestParam String houseNumber) {

        return sweepFloorService.getFamilyMembers(buildingId,unitId,houseNumber);
    }


    /**
     * 查询扫楼业务办理按钮
     * @return
     */
    @GetMapping("/getSweepFloorBizList")
    @SmIgnore
    public ApiResult<SweepFloorBizListVO> getSweepFloorBizList() {

        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        SweepFloorBizListVO bizListVO = new SweepFloorBizListVO();
        if (config == null) {
            log.error("[getSweepFloorBizList]bizList empty");
            bizListVO.setSweepFloorList(new ArrayList<>());
            bizListVO.setSweepFloorBizList(new ArrayList<>());
            return ApiResult.of(0,bizListVO);
        }
        List<StallUpBizType> sweepFloorBizList = config.getSweepFloorBizList();
        if (!CollectionUtils.isEmpty(sweepFloorBizList)) {
            bizListVO.setSweepFloorBizList(sweepFloorBizList);
        }
        List<StallUpBizType> marketToolList = new ArrayList<>();
        List<StallUpBizType> sweepFloorList = config.getSweepFloorList();
        if (!CollectionUtils.isEmpty(sweepFloorList)) {

            for (int i = 0;i < sweepFloorList.size();i++) {
                StallUpBizType bizType = sweepFloorList.get(i);
                if (bizType.getId().equals(16L)) {
                    marketToolList.add(bizType);
                    sweepFloorList.remove(i);
                    break;
                }
            }
            bizListVO.setSweepFloorList(sweepFloorList);
            bizListVO.setMarketToolList(marketToolList);
        }
        return ApiResult.of(0,bizListVO);
    }



}
