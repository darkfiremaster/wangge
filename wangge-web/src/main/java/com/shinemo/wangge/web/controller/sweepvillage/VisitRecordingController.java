package com.shinemo.wangge.web.controller.sweepvillage;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.common.GridIdChecker;
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
    @GridIdChecker
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
    @GridIdChecker
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
    @GridIdChecker
    public ApiResult<Void> deleteVisitRecording(@RequestBody SweepVillageVisitRecordingVO request) {
        Assert.notNull(request.getId(),"id is null");
        request.setStatus(0);
        return visitRecordingService.update(request);
    }

    /**
     * 根据住户id查询走访记录
     * @return
     */
    @GetMapping("/getVisitRecordingByTenantsId")
    public ApiResult<List<SweepVillageVisitRecordingVO>> getVisitRecordingByTenantsId(VisitRecordingListRequest request) {
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
    public ApiResult<ListVO<SweepVillageVisitRecordingVO>> getVisitRecordingByActivityId(VisitRecordingListRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        return visitRecordingService.getVisitRecordingByActivityId(request);
    }

    /**
     * 根据住户id查询走访记录
     * @return
     */
    @GetMapping("/getVisitRecordingDetail")
    @GridIdChecker
    public ApiResult<SweepVillageVisitRecordingVO> getVisitRecordingDetail(@RequestParam Long id) {
        Assert.notNull(id,"id is null");
        return visitRecordingService.getVisitRecordingDetail(id);
    }

}
