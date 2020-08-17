package com.shinemo.wangge.web.controller.groupserviceday;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupDO;
import com.shinemo.groupserviceday.domain.request.*;
import com.shinemo.groupserviceday.domain.vo.*;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.groupserviceday.GroupSerDayRedirctService;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayMarketingNumberService;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/groupServiceDay")
class GroupServiceDayController {


    @Resource
    private GroupServiceDayService groupServiceDayService;

    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private GroupSerDayRedirctService groupSerDayRedirctService;

    @Resource
    private GroupServiceDayMarketingNumberService groupServiceDayMarketingNumberService;

    /**
     * 获取集团列表
     */
    @GetMapping("/getGroupList")
    public ApiResult<Map<String, Object>> getGroupList(String groupName) {
        return groupServiceDayService.getGroupList(groupName);
    }

    /**
     * 新建集团服务日
     */
    @PostMapping("/createGroupServiceDay")
    public ApiResult<Void> createGroupServiceDay(@RequestBody GroupServiceDayRequest groupServiceDayRequest) {
        return groupServiceDayService.createGroupServiceDay(groupServiceDayRequest);
    }


    /**
     * 获取最近营销的集团列表
     */
    @GetMapping("/getLatestMarketingGroupList")
    public ApiResult<List<GroupDO>> getLatestMarketingGroupList() {
        return groupServiceDayService.getLatestMarketingGroupList();
    }

    /**
     * 获取首页：已结束活动、业务办理量次数
     *
     * @return
     */
    @GetMapping("/getFinishedCount")
    public ApiResult<GroupServiceDayFinishedVO> getFinishedCount(@RequestParam Integer type) {
        Assert.notNull(type, "type is null");
        return groupServiceDayService.getFinishedCount(type);
    }

    /**
     * 开始打卡
     *
     * @return
     */
    @PostMapping("/sign")
    public ApiResult<Void> sign(@RequestBody GroupServiceDaySignRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getLocationDetailVO(), "locationDetailVO is null");
        return groupServiceDayService.startSign(request);
    }

    /**
     * 结束打卡
     *
     * @return
     */
    @PostMapping("/endSign")
    public ApiResult<Void> endSign(@RequestBody GroupServiceDaySignRequest request) {
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getLocationDetailVO(), "locationDetailVO is null");
        return groupServiceDayService.endSign(request);
    }

    /**
     * 取消活动
     *
     * @return
     */
    @PostMapping("/cancel")
    public ApiResult<Void> cancel(@RequestBody GroupServiceCancelRequest request) {
        Assert.notNull(request, "id is null");
        Assert.notNull(request.getId(), "id is null");
        return groupServiceDayService.cancel(request.getId());
    }

    /**
     * 查询参与人列表
     *
     * @return
     */
    @PostMapping("/getPartnerList")
    public ApiResult<Map<String, Object>> getPartnerList(@RequestBody Map<String,Object> requestData) {
        return groupServiceDayService.getPartnerList(requestData);
    }

    /**
     * 地区查询接口
     *
     * @return
     */
    @PostMapping("/getAreaInformation")
    public ApiResult<List<GroupServiceDayAreaInfoVO>> getAreaInformation(@RequestBody Map<String,Object> requestData) {
        return groupServiceDayService.getAreaInformation(requestData);
    }


    /**
     * 集团服务日业务查询首页
     * @return
     */
    @GetMapping("/getGroupServiceDayBizDetail")
    public ApiResult<GroupServiceDayBusinessIndexVO> getGroupServiceDayBizDetail() {
        //获取配置
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        GroupServiceDayBusinessIndexVO groupServiceDayBusinessIndexVO = new GroupServiceDayBusinessIndexVO();

        //配置不存在，返回空
        if (config == null) {
            log.error("[getGroupServiceDayBizDetail]bizList empty");
            groupServiceDayBusinessIndexVO.setMarketToolList(new ArrayList<>());
            groupServiceDayBusinessIndexVO.setPublicMarketBizList(new ArrayList<>());
            groupServiceDayBusinessIndexVO.setPublicMarketBizDataList(new ArrayList<>());
            groupServiceDayBusinessIndexVO.setInformationBizList(new ArrayList<>());
            groupServiceDayBusinessIndexVO.setInformationBizDataList(new ArrayList<>());

            return ApiResult.of(0, groupServiceDayBusinessIndexVO);
        }
        //获取 营销业务列表
        groupServiceDayBusinessIndexVO.
                setMarketToolList(config.getGroupServiceDayToolList() == null ? new ArrayList<>() : config.getGroupServiceDayToolList());
        groupServiceDayBusinessIndexVO.
                setPublicMarketBizList(config.getPublicGroupServiceDayBizList() == null ? new ArrayList<>() : config.getPublicGroupServiceDayBizList());
        groupServiceDayBusinessIndexVO.
                setPublicMarketBizDataList(config.getPublicGroupServiceDayBizDataList() == null ? new ArrayList<>() : config.getPublicGroupServiceDayBizDataList());
        groupServiceDayBusinessIndexVO.
                setInformationBizList(config.getInformationGroupServiceDayBizList() == null ? new ArrayList<>() : config.getInformationGroupServiceDayBizList());
        groupServiceDayBusinessIndexVO.
                setInformationBizDataList(config.getInformationGroupServiceDayBizDataList() == null ? new ArrayList<>() : config.getInformationGroupServiceDayBizDataList());


        return ApiResult.of(0, groupServiceDayBusinessIndexVO);
    }


    /**
     * 业务列表查询接口
     * @param activityId
     * @return
     */
    @GetMapping("/getGroupServiceDayMarketNumber")
    public ApiResult<GroupServiceDayMarketNumberVO> getGroupServiceDayBizDetail(@RequestParam Long activityId) {
        return groupServiceDayMarketingNumberService.getByActivityId(activityId);
    }

    /**
     * 获取活动列表
     * @return
     */
    @GetMapping("/getActivityListByStatus")
    public ApiResult<ListVO<GroupServiceDayVO>> getActivityListByStatus(GroupServiceListRequest request) {

        return groupServiceDayService.getActivityListByStatus(request);
    }

    /**
     * 业务列表录入接口
     * @param request
     * @return
     */
    @PostMapping("/saveGroupSerPlanBusi")
    public ApiResult addBusiness(@RequestBody GroupServiceDayBusinessRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getActivityId(),"groupServiceDay activityId is null");
        return groupServiceDayMarketingNumberService.enterMarketingNumber(request);
    }



    /**
     * 集团服务日跳转企业信息url
     * @param groupId
     * @return
     */
    @GetMapping("getRedirctGrouSerUrl")
    @SmIgnore
    public ApiResult<String> redirctGroupServiceInfo(@RequestParam String groupId) {
        Assert.hasText(groupId, "groupId is null");
        return groupSerDayRedirctService.getRedirctGrouSerUrl(groupId);
    }

    @GetMapping("getRedirctSmsHotUrl")
    @SmIgnore
    public ApiResult<String> redirctSmsHot(@RequestParam Long activityId) {
        Assert.notNull(activityId, "id is null");
        return groupSerDayRedirctService.getRedirctSmsHotUrl(activityId);
    }

}
