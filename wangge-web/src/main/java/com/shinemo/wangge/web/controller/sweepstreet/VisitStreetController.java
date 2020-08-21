package com.shinemo.wangge.web.controller.sweepstreet;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.common.GridIdChecker;
import com.shinemo.sweepstreet.domain.query.SweepStreetVisitRecordingQuery;
import com.shinemo.sweepstreet.domain.vo.SweepStreetVisitRecordingVO;
import com.shinemo.wangge.core.service.sweepstreet.VisitStreetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ChenQinHai
 * @date 2020/8/13
 */
@Slf4j
@RestController
    @RequestMapping("/sweepStreet")
public class VisitStreetController {
    @Resource
    private VisitStreetService visitStreetService;


    @PostMapping("/addVisitStreet")
    @GridIdChecker
    public ApiResult<Void> addVisitStreet(@RequestBody SweepStreetVisitRecordingVO request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"activityId is null");
        Assert.notNull(request.getMerchantId(),"merchantId is null");
        Assert.notNull(request.getContactName(),"contactName is null");
        Assert.notNull(request.getContactMobile(),"contactMobile is null");
        return visitStreetService.add(request);
    }

    /**
     * 根据商户id查询走访记录列表
     * @param merchantId
     * @return
     */
    @GetMapping("/getVisitStreetByMerchantId")
    public ApiResult getVisitStreetByMerchantId( String merchantId,Long activityId){
        Assert.notNull(merchantId,"merchantId is null");
        Assert.notNull(activityId,"activityId is null");

        SweepStreetVisitRecordingQuery query =new SweepStreetVisitRecordingQuery();
        query.setMerchantId(merchantId);
        query.setStatus(1);
        query.setOrderByEnable(true);
        query.putOrderBy("gmt_create",false);
        return visitStreetService.getVisitStreetByMerchantId(query,activityId);
    }

    /**
     * 根据活动id查询走访记录列表
     * @param activityId
     * @return
     */
    @GetMapping("/getVisitStreetByActivityId")
    public ApiResult getVisitStreetByActivityId(Long activityId){
        Assert.notNull(activityId,"activityId is null");
        SweepStreetVisitRecordingQuery query =new SweepStreetVisitRecordingQuery();
        query.setActivityId(activityId);
        query.setStatus(1);
        query.setOrderByEnable(true);
        query.putOrderBy("gmt_create",false);
        return visitStreetService.getVisitStreetByActivityId(query);
    }

    /**
     * 获取走访记录详情
     * @param id
     * @return
     */
    @GetMapping("/getVisitStreetDetail")
    public  ApiResult getVisitStreetDetail( Long id){
        Assert.notNull(id,"id is null");
        return visitStreetService.getVisitStreetDetail(id);
    }

    /**
     * 获取商户业务详情
     * @param merchantId
     * @return
     */
    @GetMapping("/getBusinessDetail")
    public  ApiResult getBusinessDetail( String merchantId){
        Assert.notNull(merchantId,"merchantId is null");
        return visitStreetService.getBusinessDetail(merchantId);
    }
}
