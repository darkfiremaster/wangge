package com.shinemo.wangge.web.controller.sweepvillage;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.sweepvillage.domain.SweepVillageActivityDO;
import com.shinemo.sweepvillage.vo.VillageVO;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

//    @GetMapping("/getSweepVillageActivityList")
//    public ApiResult<List<VillageVO>>
}
