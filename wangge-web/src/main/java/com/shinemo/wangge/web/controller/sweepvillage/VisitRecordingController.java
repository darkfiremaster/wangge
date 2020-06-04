package com.shinemo.wangge.web.controller.sweepvillage;


import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.wangge.core.service.sweepvillage.VisitRecordingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/sweepvillage")
public class VisitRecordingController {

    @Resource
    private VisitRecordingService visitRecordingService;
    /**
     * 更新扫楼活动状态
     * @param request
     * @return
     */
    @PostMapping("/addVisitRecording")
    @SmIgnore
    public ApiResult<Void> addVisitRecording(@RequestBody SweepVillageVisitRecordingVO request) {
       return visitRecordingService.addVisitRecording(request);
    }

}
