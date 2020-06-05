package com.shinemo.wangge.web.controller.thirdapi;

import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiCacheManager;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 10:28
 * @Desc
 */
@Slf4j
@RequestMapping("/thirdapi")
@RestController
public class ThirdApiMappingController {

    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    @Resource
    private ThirdApiCacheManager thirdApiCacheManager;

    /**
     * 第三方接口转发
     * @return
     */
    @PostMapping("/dispatch/{apiName}")
    @SmIgnore
    public ApiResult<Map<String,Object>> dispatch(@PathVariable String apiName, @RequestBody Map<String,Object> requestData) {
        Assert.notNull(apiName,"apiName is null");
        Assert.notNull(requestData,"request is null");

        return thirdApiMappingService.dispatch(requestData, apiName);
    }


    /**
     * 重置缓存
     * @return
     */
    @GetMapping("/clearCache")
    @SmIgnore
    public ApiResult<String> reload() {
        thirdApiCacheManager.reload();
        return ApiResult.success("success");
    }

    /**
     * @return
     */
    @GetMapping("/test")
    @SmIgnore
    public ApiResult<String> test() {
        return ApiResult.success("success");
    }

}
