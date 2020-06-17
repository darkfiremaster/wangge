package com.shinemo.wangge.web.controller.common;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.smartgrid.utils.RedisKeyUtil;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.schedule.EndStallUpSchedule;
import com.shinemo.wangge.core.schedule.GetGridMobileSchedule;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.core.service.sweepfloor.SweepFloorService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 后门
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@RestController
@RequestMapping("backdoor")
@Slf4j
public class BackdoorController {

    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private GetGridMobileSchedule getGridMobileSchedule;

    @Resource
    private EndStallUpSchedule endStallUpSchedule;

    @Resource
    private ThirdApiCacheManager thirdApiCacheManager;

    @Resource
    private SweepFloorService sweepFloorService;

    @Resource
    private LoginStatisticsService loginStatisticsService;



    @Resource
    private RedisService redisService;

    @GetMapping("stallUp/config/flush")
    public String flushConfig() {
        try {
            stallUpConfig.init();
        } catch (Exception e) {
            e.printStackTrace();
            return "check error:" + e.getMessage();
        }
        return "success\n";
    }

    @GetMapping("smartGrid/get/mobile")
    public String getMobile() {
        getGridMobileSchedule.execute();
        return "success\n";
    }

    @GetMapping("smartGrid/stallUp/end")
    public String stallUpEnd() {
        endStallUpSchedule.execute();
        return "success\n";
    }

    @GetMapping("/thirdapi/reloadCache")
    public String reload() {
        thirdApiCacheManager.reload();
        return "success\n";
    }

//	@GetMapping("smartGrid/refresh/smart_grid_activity/{mobile}")
//	public String refreshSmartGridActivity(@PathVariable(value = "mobile") String mobile) {
//		Assert.notNull(mobile,"mobile is null");
//		sweepFloorService.refreshSmartGridActivity(mobile);
//		return "success\n";
//	}

    /**
     * 订正扫楼活动网格id
     *
     * @return
     */
    @PostMapping("/smartGrid/refresh/smart_grid_activity")
    public String refreshSmartGridActivity(@RequestBody List<String> mobiles) {
        Assert.isTrue(!CollectionUtils.isEmpty(mobiles), "mobiles is null");
        for (String mobile : mobiles) {
            sweepFloorService.refreshSmartGridActivity(mobile);
        }
        return "success\n";
    }

    /**
     * 生成昨日登录统计结果
     * @return
     */
    @GetMapping("/generateLoginInfoResult")
    public ApiResult<List<LoginInfoResultDO>> generateLoginInfoResult() {
        return loginStatisticsService.saveYesterdayLoginInfoResult();
    }

    /**
     * 清空用户网格信息缓存
     * @param mobile
     * @return
     */
    @GetMapping(value = "/clearUserGridInfoCache")
    public ApiResult<Void> clearUserGridInfoCache(String mobile) {
        redisService.del(RedisKeyUtil.getUserGridInfoPrefixKey(mobile));
        log.info("[clearUserGridInfoCache] 清空用户网格信息缓存成功,mobile:{}", mobile);
        return ApiResult.of(0);
    }




}
