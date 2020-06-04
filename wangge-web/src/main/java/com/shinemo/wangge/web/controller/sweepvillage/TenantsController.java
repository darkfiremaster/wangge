package com.shinemo.wangge.web.controller.sweepvillage;

import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.vo.SweepVillageTenantsVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/sweepvillage")
public class TenantsController {

    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    /**
     * 新增走访记录
     * @param request
     * @return
     */
//    @PostMapping("/addTenants")
//    @SmIgnore
//    public ApiResult<Void> addVisitRecording(@RequestBody SweepVillageTenantsVO request) {
//        thirdApiMappingService.dispatch()
//    }

}
