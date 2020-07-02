package com.shinemo.wangge.web.controller.common;

import cn.hutool.core.util.StrUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.excel.LoginInfoExcelDTO;
import com.shinemo.smartgrid.domain.model.BackdoorLoginDO;
import com.shinemo.stallup.domain.model.ParentStallUpActivity;
import com.shinemo.stallup.domain.model.StallUpCommunityDO;
import com.shinemo.stallup.domain.query.ParentStallUpActivityQuery;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.schedule.EndStallUpSchedule;
import com.shinemo.wangge.core.schedule.GetGridMobileSchedule;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.core.service.sweepfloor.SweepFloorService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiCacheManager;
import com.shinemo.wangge.dal.mapper.BackdoorLoginMapper;
import com.shinemo.wangge.dal.mapper.ParentStallUpActivityMapper;
import com.shinemo.wangge.dal.mapper.StallUpCommunityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private BackdoorLoginMapper backdoorLoginMapper;
    @Resource
    private ParentStallUpActivityMapper parentStallUpActivityMapper;

    @Resource
    private StallUpCommunityMapper stallUpCommunityMapper;


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
     *
     * @return
     */
    @GetMapping("/generateLoginInfoResult")
    public ApiResult<List<LoginInfoResultDO>> generateLoginInfoResult() {
        return loginStatisticsService.saveYesterdayLoginInfoResult();
    }


    /**
     * 获取昨日登录信息
     *
     * @return
     */
    @GetMapping("/getLoginInfoExcelDTOList")
    public ApiResult<List<LoginInfoExcelDTO>> getLoginInfoExcelDTOList() {
        return loginStatisticsService.getLoginInfoExcelDTOList();
    }

    /**
     * 模拟登陆
     */
    @GetMapping("/mockLogin")
    public ApiResult<Void> mockLogin(String loginMobile, String mockMobile) {
        Assert.notNull(loginMobile, "loginMobile is null");
        backdoorLoginMapper.deleteByMobile(loginMobile);
        if (StrUtil.isBlank(mockMobile)) {
            return ApiResult.of(0);
        }
        BackdoorLoginDO backdoorLoginDO = new BackdoorLoginDO();
        backdoorLoginDO.setMobile(loginMobile);
        backdoorLoginDO.setCUid(String.valueOf(0));
        backdoorLoginDO.setCOrgId(String.valueOf(0));
        backdoorLoginDO.setCOrgName("mock企业");
        backdoorLoginDO.setCUserName("mock账号");
        backdoorLoginDO.setCMobile(mockMobile);

        backdoorLoginMapper.insert(backdoorLoginDO);

        return ApiResult.of(0);
    }

    @GetMapping("/stallUp/refreshCommunity")
    public String refreshCommunity() {
        ParentStallUpActivityQuery parentStallUpActivityQuery = new ParentStallUpActivityQuery();
        List<ParentStallUpActivity> historyForRefreshCommunity = parentStallUpActivityMapper.findHistoryForRefreshCommunity(parentStallUpActivityQuery);
        if (!CollectionUtils.isEmpty(historyForRefreshCommunity)) {
            List<StallUpCommunityDO> stallUpCommunityDOS = new ArrayList<>(historyForRefreshCommunity.size());
            for (ParentStallUpActivity stallUpActivity : historyForRefreshCommunity) {
                StallUpCommunityDO stallUpCommunityDO = new StallUpCommunityDO();
                stallUpCommunityDO.setActivityId(stallUpActivity.getId());
                stallUpCommunityDO.setCommunityName(stallUpActivity.getCommunityName());
                stallUpCommunityDO.setCommunityLocation(stallUpActivity.getLocation());
                stallUpCommunityDO.setCommunityId(stallUpActivity.getCommunityId());
                stallUpCommunityDO.setCommunityAddress(stallUpActivity.getAddress());
                stallUpCommunityDOS.add(stallUpCommunityDO);
            }
            for (int i = 0; i < stallUpCommunityDOS.size(); i += 50) {
                stallUpCommunityMapper.batchInsert(stallUpCommunityDOS.subList(i, Math.min(i + 50, stallUpCommunityDOS.size())));
            }
        }
        return "success\n";
    }

}
