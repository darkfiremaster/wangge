package com.shinemo.wangge.web.controller.groupserviceday;


import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayPartnerListRequest;
import com.shinemo.groupserviceday.domain.request.GroupServiceDaySignRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBusinessIndexVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayFinishedVO;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.sweepvillage.domain.vo.SweepVillageBizListVO;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/groupServiceDay")
public class GroupServiceDayController {


    @Resource
    private GroupServiceDayService groupServiceDayService;

    @Resource
    private StallUpConfig stallUpConfig;

    /**
     * 获取首页：已结束活动、业务办理量次数
     * @return
     */
    @GetMapping("/getFinishedCount")
    public ApiResult<GroupServiceDayFinishedVO> getFinishedCount(@RequestParam Integer type) {
        Assert.notNull(type,"type is null");
        return groupServiceDayService.getFinishedCount(type);
    }

    /**
     * 开始打卡
     * @return
     */
    @PostMapping("/sign")
    public ApiResult<Void> sign(@RequestBody GroupServiceDaySignRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getLocation(),"location is null");
        return groupServiceDayService.startSign(request);
    }

    /**
     * 结束打卡
     * @return
     */
    @PostMapping("/endSign")
    public ApiResult<Void> endSign(@RequestBody GroupServiceDaySignRequest request) {
        Assert.notNull(request,"request is null");
        Assert.notNull(request.getLocation(),"location is null");
        return groupServiceDayService.endSign(request);
    }

    /**
     * 取消活动
     * @return
     */
    @PostMapping("/cancel")
    public ApiResult<Void> cancel(@RequestBody Long id) {
        Assert.notNull(id,"id is null");
        return groupServiceDayService.cancel(id);
    }

    /**
     * 查询参与人列表
     * @return
     */
    @GetMapping("/getPartnerList")
    public ApiResult<Map<String,Object>> getPartnerList(GroupServiceDayPartnerListRequest request) {
        return groupServiceDayService.getPartnerList(request);
    }


    @GetMapping("/getGroupServiceDayBizDetail")
    public ApiResult<GroupServiceDayBusinessIndexVO> getGroupServiceDayBizDetail() {
        //获取摆摊配置
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

            return ApiResult.of(0,groupServiceDayBusinessIndexVO);
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


        return ApiResult.of(0,groupServiceDayBusinessIndexVO);
    }
}
