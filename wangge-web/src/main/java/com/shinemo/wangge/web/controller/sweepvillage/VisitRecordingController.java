package com.shinemo.wangge.web.controller.sweepvillage;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.request.VisitRecordingListRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.wangge.core.service.sweepvillage.VisitRecordingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sweepvillage")
public class VisitRecordingController {

    @Resource
    private VisitRecordingService visitRecordingService;
    /**
     * 新增走访记录
     * @param request
     * @return
     */
    @PostMapping("/addVisitRecording")
    @SmIgnore
    public ApiResult<Void> addVisitRecording(@RequestBody SweepVillageVisitRecordingVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getTenantsId(),"tenantsId is null");
        Assert.notNull(request.getContactName(),"contactName is null");
        Assert.notNull(request.getContactMobile(),"contactMobile is null");
        return visitRecordingService.add(request);
    }

    /**
     * 编辑走访记录
     * @param request
     * @return
     */
    @PostMapping("/updateVisitRecording")
    @SmIgnore
    public ApiResult<Void> updateVisitRecording(@RequestBody SweepVillageVisitRecordingVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getTenantsId(),"tenantsId is null");
        Assert.notNull(request.getContactName(),"contactName is null");
        Assert.notNull(request.getContactMobile(),"contactMobile is null");
        return visitRecordingService.update(request);
    }

    /**
     * 删除走访记录
     * @return
     */
    @PostMapping("/deleteVisitRecording")
    @SmIgnore
    public ApiResult<Void> deleteVisitRecording(@RequestBody Long id) {
        Assert.notNull(id,"id is null");
        SweepVillageVisitRecordingVO request = new SweepVillageVisitRecordingVO();
        request.setId(id);
        request.setStatus(0);
        return visitRecordingService.update(request);
    }

    /**
     * 根据住户id查询走访记录
     * @return
     */
    @GetMapping("/getVisitRecordingByTenantsId")
    @SmIgnore
    public ApiResult<List<SweepVillageVisitRecordingVO>> getVisitRecordingByTenantsId(@RequestBody VisitRecordingListRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getTenantsId(),"tenantsId is null");
        return visitRecordingService.getVisitRecordingByTenantsId(request);
    }

    /**
     * 根据住户id查询走访记录
     * @return
     */
    @GetMapping("/getVisitRecordingByActivityId")
    @SmIgnore
    public ApiResult<ListVO<SweepVillageVisitRecordingVO>> getVisitRecordingByActivityId(@RequestBody VisitRecordingListRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        return visitRecordingService.getVisitRecordingByActivityId(request);
    }

}
