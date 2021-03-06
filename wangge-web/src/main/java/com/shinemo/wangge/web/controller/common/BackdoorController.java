package com.shinemo.wangge.web.controller.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.user.UserProfileServiceWrapper;
import com.shinemo.client.ace.user.domain.UserProfileInfo;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.smartgrid.domain.model.BackdoorLoginDO;
import com.shinemo.smartgrid.domain.model.UserConfigDO;
import com.shinemo.smartgrid.domain.query.UserConfigQuery;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.ParentStallUpActivity;
import com.shinemo.stallup.domain.model.StallUpCommunityDO;
import com.shinemo.stallup.domain.query.ParentStallUpActivityQuery;
import com.shinemo.stallup.domain.query.StallUpCommunityQuery;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.schedule.*;
import com.shinemo.wangge.core.service.common.ExcelService;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import com.shinemo.wangge.core.service.sweepfloor.SweepFloorService;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiCacheManager;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserConfigMapper userConfigMapper;

    @Resource
    private BackdoorLoginMapper backdoorLoginMapper;
    @Resource
    private ParentStallUpActivityMapper parentStallUpActivityMapper;

    @Resource
    private StallUpCommunityMapper stallUpCommunityMapper;

    @Resource
    private SweepVillageActivityService sweepVillageActivityService;

    @Resource
    private SignRecordMapper signRecordMapper;

    @Resource
    private UserProfileServiceWrapper userProfileServiceWrapper;

    @Resource
    private ExcelService excelService;

    @Resource
    private RedisService redisService;

    @Resource
    private SweepVillageVisitRecordingMapper sweepVillageVisitRecordingMapper;

    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    @Resource
    private EndGroupServiceDaySchedule endGroupServiceDaySchedule;

    @Resource
    private EndStreetServiceSchedule endStreetServiceSchedule;

    @Resource
    private YuJingWarnSchedule yuJingWarnSchedule;

    private final AppTypeEnum appType = AppTypeEnum.GUANGXI;

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
        log.info("[refreshCommunity] 历史摆摊数量:{}", historyForRefreshCommunity.size());
        if (!CollectionUtils.isEmpty(historyForRefreshCommunity)) {
            List<StallUpCommunityDO> stallUpCommunityDOS = new ArrayList<>(historyForRefreshCommunity.size());
            for (ParentStallUpActivity stallUpActivity : historyForRefreshCommunity) {
                //根据活动id和小区id判断,数据是否已存在
                StallUpCommunityQuery stallUpCommunityQuery = new StallUpCommunityQuery();
                stallUpCommunityQuery.setActivityId(stallUpActivity.getId());
                stallUpCommunityQuery.setCommunityId(stallUpActivity.getCommunityId());
                StallUpCommunityDO old = stallUpCommunityMapper.get(stallUpCommunityQuery);

                if (old == null) {
                    StallUpCommunityDO stallUpCommunityDO = new StallUpCommunityDO();
                    stallUpCommunityDO.setActivityId(stallUpActivity.getId());
                    stallUpCommunityDO.setCommunityName(stallUpActivity.getCommunityName());
                    stallUpCommunityDO.setCommunityLocation(stallUpActivity.getLocation());
                    stallUpCommunityDO.setCommunityId(stallUpActivity.getCommunityId());
                    stallUpCommunityDO.setCommunityAddress(stallUpActivity.getAddress());
                    stallUpCommunityDOS.add(stallUpCommunityDO);
                }
            }

            log.info("[refreshCommunity] 需要更新的数量:{}", stallUpCommunityDOS.size());
            for (int i = 0; i < stallUpCommunityDOS.size(); i += 50) {
                stallUpCommunityMapper.batchInsert(stallUpCommunityDOS.subList(i, Math.min(i + 50, stallUpCommunityDOS.size())));
            }
        }
        return "success\n";
    }

    /**
     * 订正扫村活动数据库数据
     *
     * @return
     */
    @GetMapping("/sweepVillage/fixDatabase")
    public String fixDatabase() {
        ApiResult<Void> voidApiResult = sweepVillageActivityService.fixDBWithCreatorName();
        return voidApiResult.getMsg();

    }

    /**
     * 订正扫楼创建人名
     *
     * @return
     */
    @GetMapping("/sweepFloor/fixDatabase")
    public String fixSweepFloor() {
        ApiResult<Void> voidApiResult = sweepFloorService.fixSweepFloor();
        return voidApiResult.getMsg();
    }

    /**
     * 订正签到表手机号、签到人名字段
     *
     * @return
     */
    @GetMapping("/fixSignRecord")
    public String fixSignRecord() {
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        List<SignRecordDO> signRecordDOS = signRecordMapper.find(signRecordQuery);
        if (CollectionUtils.isEmpty(signRecordDOS)) {
            log.error("[fixSignRecord] signRecordDOS is empty");
        }
        int count = 0;
        for (SignRecordDO signRecordDO : signRecordDOS) {
            UserProfileInfo userProfileInfo = userProfileServiceWrapper.getUserProfileInfo(signRecordDO.getUserId(), new AaceContext(appType.getId() + ""));
            if (userProfileInfo != null) {
                SignRecordDO updateDO = new SignRecordDO();
                String mobile = userProfileInfo.getMobile();
                String name = userProfileInfo.getName();
                updateDO.setMobile(mobile);
                updateDO.setUserName(name);
                updateDO.setId(signRecordDO.getId());
                signRecordMapper.update(updateDO);
                count++;
            } else {
                log.error("[fixSignRecord] userProfileInfo is null,uid = {}", signRecordDO.getUserId());
            }
        }
        log.info("[fixSignRecord] fixSignRecord finished,count = {}", count);
        return "success";
    }


    /**
     * 订正t_user_config表里grid_biz字段,去除指定的id
     *
     * @param id 需要去除的id
     * @return
     */
    @GetMapping("/fixUserConfig")
    public String fixUserConfig(Integer id) {
        List<UserConfigDO> userConfigDOS = userConfigMapper.find(new UserConfigQuery());
        int count = 0;
        if (CollUtil.isNotEmpty(userConfigDOS)) {

            for (UserConfigDO userConfigDO : userConfigDOS) {
                String gridBiz = userConfigDO.getGridBiz();
                List<Integer> gridBizList = JSONUtil.parseArray(gridBiz).toList(Integer.class);
                if (gridBizList.contains(id)) {
                    gridBizList.remove(id);
                    userConfigDO.setGridBiz(GsonUtils.toJson(gridBizList));
                    userConfigMapper.update(userConfigDO);
                    count++;
                }
            }
            log.info("[fixUserConfig] 订正数据的数量:{}", count);
        }
        return "success";
    }

    /**
     *
     * @return
     */
    @GetMapping("/refreshSweepVillageVisit")
    public String refreshSweepVillageVisit() {
        SweepVillageVisitRecordingQuery query = new SweepVillageVisitRecordingQuery();
        List<SweepVillageVisitRecordingDO> recordingDOS = sweepVillageVisitRecordingMapper.refreshCreateMobile(query);
        if (CollectionUtils.isEmpty(recordingDOS)) {
            return "success";
        }
        int count = 0;
        for (SweepVillageVisitRecordingDO visitRecordingDO: recordingDOS) {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id",visitRecordingDO.getTenantsId());
            requestData.put("mobile",visitRecordingDO.getMobile());
            ApiResult<Map<String, Object>> apiResult = thirdApiMappingService.dispatch(requestData, "getTenantsDetail");
            if (!apiResult.isSuccess()) {
                log.error("[refreshSweepVillageVisit] api error, tenantsId = {},mobile = {}",visitRecordingDO.getTenantsId(),visitRecordingDO.getMobile());
                continue;
            }
            Map<String, Object> data = apiResult.getData();
            if (!CollectionUtils.isEmpty(data)) {
                SweepVillageVisitRecordingDO updateDO = new SweepVillageVisitRecordingDO();
                updateDO.setId(visitRecordingDO.getId());
                updateDO.setCreateTenantsMobile((String)data.get("createMobile"));
                sweepVillageVisitRecordingMapper.update(updateDO);
                count++;
            }
        }
        log.info("[refreshSweepVillageVisit] refresh finished,count = {}",count);
        return "success";
    }

    @GetMapping("groupServiceDay/autoEnd")
    public String groupServiceDayEnd() {
        endGroupServiceDaySchedule.execute();
        return "success\n";
    }

    @GetMapping("sweepStreet/autoEnd")
    public String sweepStreetEnd() {
        endStreetServiceSchedule.execute();
        return "success\n";
    }

    @GetMapping("todo/yuJingTimeOutWarn")
    public String yujingTimeOutWarn() {
        yuJingWarnSchedule.yujingTodoTimeoutWarn();
        return "success\n";
    }

}

