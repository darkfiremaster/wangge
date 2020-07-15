package com.shinemo.wangge.web.controller.common;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.JsonElement;
import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.GetSimpleInfoResponse;
import com.shinemo.smartgrid.domain.OutsideMarketingNumVO;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.HttpUtil;
import com.shinemo.stallup.common.enums.BizGroupEnum;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.stallup.domain.model.BizTypeListVO;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.model.SweepFloorBizTotal;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.stallup.domain.response.GetParentSimpleResponse;
import com.shinemo.stallup.domain.response.GetStallUpSimpleInfoResponse;
import com.shinemo.sweepfloor.common.enums.SweepFloorStatusEnum;
import com.shinemo.sweepfloor.domain.response.IndexInfoResponse;
import com.shinemo.sweepfloor.domain.vo.SmartGridVO;
import com.shinemo.sweepfloor.domain.vo.SweepFloorActivityVO;
import com.shinemo.sweepfloor.domain.vo.SweepFloorBusinessCountAndHouseCountVO;
import com.shinemo.sweepvillage.domain.request.SweepVillageActivityQueryRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityFinishVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageActivityResultVO;
import com.shinemo.sweepvillage.enums.SweepVillageStatusEnum;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.stallup.StallUpService;
import com.shinemo.wangge.core.service.sweepfloor.SweepFloorService;
import com.shinemo.wangge.core.service.sweepvillage.SweepVillageActivityService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@RestController
@RequestMapping("smartGrid")
@Slf4j
public class IndexController {

    @Resource
    private StallUpService stallUpService;

    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private SweepFloorService sweepFloorService;

    @Resource
    private SweepVillageActivityService sweepVillageActivityService;

    @Resource
    private HuaWeiService huaWeiService;

    @GetMapping("getIndex")
    @SmIgnore
    public ApiResult<GetSimpleInfoResponse> getIndex() {
        long uid = SmartGridContext.getLongUid();
        String mobile = SmartGridContext.getMobile();
        ApiResult<GetStallUpSimpleInfoResponse> stallUpResponse = stallUpService.getSimpleInfo(mobile);
        if (!stallUpResponse.isSuccess()) {
            log.error("[getIndex] failed call stallUpService.getSimpleInfo, mobile:{}", mobile);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        GetStallUpSimpleInfoResponse stallUpInfo = stallUpResponse.getData();
        GetSimpleInfoResponse response = new GetSimpleInfoResponse();
        response.setStallUpStartedDetail(stallUpInfo.getStartedDetail());
        response.setStallUpWeekDetail(stallUpInfo.getWeekDetail());

        // 查询扫楼相关信息
        ApiResult<IndexInfoResponse> sweepFloorServiceIndexInfo = sweepFloorService.getIndexInfo(uid);
        if (sweepFloorServiceIndexInfo == null || !sweepFloorServiceIndexInfo.isSuccess()) {
            log.error("[getIndex] failed call sweepFloorService.getIndexInfo, uid:{}", uid);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        ApiResult<SweepFloorBusinessCountAndHouseCountVO> businessCountAndHouseCount = sweepFloorService
                .getBusinessCountAndHouseCount(1);
        SweepFloorBizTotal sweepFloorBizTotal = new SweepFloorBizTotal();
        if (businessCountAndHouseCount != null && businessCountAndHouseCount.isSuccess()) {
            SweepFloorBusinessCountAndHouseCountVO countData = businessCountAndHouseCount.getData();
            sweepFloorBizTotal.setBusinessCount(countData.getBusinessCount());
            sweepFloorBizTotal.setHouseCount(countData.getHouseCount());
            response.setSweepFloorBizTotal(sweepFloorBizTotal);
        } else {
            sweepFloorBizTotal.setBusinessCount(0);
            sweepFloorBizTotal.setHouseCount(0);
        }
        IndexInfoResponse sweepFloorData = sweepFloorServiceIndexInfo.getData();
        // 加上扫楼
        response.setTodayToDo(stallUpInfo.getTodayToDo() + sweepFloorData.getTodayToDo());
        response.setMonthDone(stallUpInfo.getMonthDone() + sweepFloorData.getMonthDone());
        response.setWeekToDo(stallUpInfo.getWeekToDo() + sweepFloorData.getWeekToDo());
        response.setSweepFloorActivityVO(sweepFloorData.getSweepFloorActivityVO());

        // 查扫村相关信息
        SweepVillageActivityQueryRequest sweepVillageActivityQueryRequest = new SweepVillageActivityQueryRequest();
        sweepVillageActivityQueryRequest.setStatus(SweepVillageStatusEnum.PROCESSING.getId());
        ApiResult<ListVO<SweepVillageActivityResultVO>> sweepVillageActivityList = sweepVillageActivityService.getSweepVillageActivityList(sweepVillageActivityQueryRequest);
        if (CollUtil.isNotEmpty(sweepVillageActivityList.getData().getRows())) {
            SweepVillageActivityResultVO sweepVillageActivityResultVO = sweepVillageActivityList.getData().getRows().get(0);
            response.setSweepVillageActivityResultVO(sweepVillageActivityResultVO);
        } else {
            response.setSweepVillageActivityResultVO(null);
        }
        ApiResult<SweepVillageActivityFinishVO> finishResultInfo = sweepVillageActivityService.getFinishResultInfo(1);
        response.setSweepVillageActivityFinishVO(finishResultInfo.getData());

        // 首页
        List<StallUpBizType> homePageBizList = stallUpService.getGridBiz(uid + "").stream()
                .map(i -> toBizTypeVO(stallUpConfig.getConfig().getMap().get(i))).collect(Collectors.toList());
        response.setHomePageBizList(homePageBizList);
        // 督导
        response.setDuDaoTopId(24L);
        response.setDuDaoBottomId(25L);
        UrlRedirectHandlerRequest request = new UrlRedirectHandlerRequest();
        request.setId(26L);
        request.setUserPhone(SmartGridContext.getMobile());
        ApiResult<String> urlResult = huaWeiService.getRedirectUrl(request);
        if (urlResult.isSuccess()) {
            try {
                String json = HttpUtil.get(urlResult.getData(), true, 2000);
                GetSimpleInfoResponse.DuDaoQueryResult result = GsonUtils.fromGson2Obj(json,
                        GetSimpleInfoResponse.DuDaoQueryResult.class);
                GetSimpleInfoResponse.DuDaoQueryResultVO resultVO = new GetSimpleInfoResponse.DuDaoQueryResultVO();
                if (result != null && result.getStatus() == 0) {
                    resultVO.setTaskTotal(result.getTaskTotal());
                    JsonElement taskInfo = result.getTaskInfo();
                    if (taskInfo.isJsonObject()) {
                        GetSimpleInfoResponse.TaskInfo value = GsonUtils.fromGson2Obj(
                                taskInfo.getAsJsonObject().toString(), GetSimpleInfoResponse.TaskInfo.class);
                        resultVO.setTaskInfo(value);
                    }
                    response.setDuDaoResult(resultVO);
                }
            } catch (Exception e) {
                log.error("[getIndex] throw exception", e);
            }
        }
        return ApiResult.of(0, response);
    }

    /**
     * 查询场外营销扫楼、摆摊数量
     *
     * @return
     */
    @GetMapping("/getOutsideMarketing")
    @SmIgnore
    public ApiResult<List<OutsideMarketingNumVO>> getOutsideMarketing() {
        String mobile = SmartGridContext.getMobile();
        ApiResult<List<GridUserRoleDetail>> gridUserInfo = null;
        try {
            gridUserInfo = huaWeiService.getGridUserInfo(HuaWeiRequest.builder().mobile(mobile).build());
        } catch (ApiException e) {
            log.error("[getOutsideMarketing]getGridUserInfo error,mobile = {}", mobile);
            return ApiResult.of(0, new ArrayList<>());
        }

        if (gridUserInfo == null || !gridUserInfo.isSuccess()) {
            return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
        }
        List<GridUserRoleDetail> userInfoData = gridUserInfo.getData();
        // List<GridUserRoleDetail> userInfoData = new ArrayList<>();
        // GridUserRoleDetail detail = new GridUserRoleDetail();
        // detail.setId("783_A2417_06");
        // detail.setName("浙江网格");
        // GridUserRoleDetail detail02 = new GridUserRoleDetail();
        // detail02.setId("783_A2417_061");
        // detail02.setName("广西网格");
        // userInfoData.add(detail);
        // userInfoData.add(detail02);
        if (CollectionUtils.isEmpty(userInfoData)) {
            return ApiResult.of(0, new ArrayList<>());
        }
        List<String> gridIds = userInfoData.stream().map(GridUserRoleDetail::getId).collect(Collectors.toList());

        // 查询扫楼相关
        ApiResult<ListVO<SmartGridVO>> sweepFloorResult = sweepFloorService.getSweepFloorActivityByGridIds(gridIds,
                false, null, null);
        if (!sweepFloorResult.isSuccess()) {
            return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
        }
        ListVO<SmartGridVO> gridVOListVO = sweepFloorResult.getData();
        List<SmartGridVO> smartGridVOS = gridVOListVO.getRows();
        // 查询摆摊相关
        ApiResult<GetParentSimpleResponse> stallUpResult = stallUpService.getParentSimple(gridIds);
        if (!stallUpResult.isSuccess()) {
            return ApiResultWrapper.fail(StallUpErrorCodes.BASE_ERROR);
        }
        Map<String, Long> prepareMap = stallUpResult.getData().getPrepareMap();
        Map<String, Long> startMap = stallUpResult.getData().getStartMap();
        Map<String, Long> endMap = stallUpResult.getData().getEndMap();
        List<OutsideMarketingNumVO> numVOS = new ArrayList<>();
        for (GridUserRoleDetail gridUserRoleDetail : userInfoData) {
            OutsideMarketingNumVO outsideMarketingNumVO = new OutsideMarketingNumVO();
            String gridId = gridUserRoleDetail.getId();
            outsideMarketingNumVO.setGridId(gridId);
            outsideMarketingNumVO.setGridName(gridUserRoleDetail.getName());
            if (CollectionUtils.isEmpty(smartGridVOS)) {
                outsideMarketingNumVO.setSweepFloorEndCount(0);
                outsideMarketingNumVO.setSweepFloorNotStartCount(0);
                outsideMarketingNumVO.setSweepFloorStartedCount(0);
            } else {
                buildSweepFloorCount(smartGridVOS, gridUserRoleDetail, outsideMarketingNumVO);
            }
            // 拼装摆摊数据
            outsideMarketingNumVO.setStallUpNotStartCount(Math.toIntExact(prepareMap.getOrDefault(gridId, 0L)));
            outsideMarketingNumVO.setStallUpStartedCount(Math.toIntExact(startMap.getOrDefault(gridId, 0L)));
            outsideMarketingNumVO.setStallUpEndCount(Math.toIntExact(endMap.getOrDefault(gridId, 0L)));
            numVOS.add(outsideMarketingNumVO);
        }

        return ApiResult.of(0, numVOS);

    }

    @GetMapping("/getAllHomePageBizList")
    @SmIgnore
    public ApiResult<BizTypeListVO> getAllHomePageBizList() {
        List<StallUpBizType> homePageBizList = stallUpService.getGridBiz(SmartGridContext.getUid()).stream()
                .map(i -> toBizTypeVO(stallUpConfig.getConfig().getMap().get(i))).collect(Collectors.toList());
        List<StallUpBizType> indexList = stallUpConfig.getConfig().getIndexList();
        List<StallUpBizType> quickAccessList = stallUpConfig.getConfig().getQuickAccessList();
        List<StallUpBizType> daosanjiaoSupportBizList = stallUpConfig.getConfig().getDaosanjiaoSupportBizList();

        //拼接到一个List返回给前端
        List<BizTypeListVO.BizTypeBean> bizTypeListBean = new ArrayList<>();

        BizTypeListVO.BizTypeBean homePageBean = new BizTypeListVO.BizTypeBean();
        homePageBean.setBizGroupName(BizGroupEnum.COMMON_APPLICATION.getName());
        homePageBean.setBizTypeList(homePageBizList);
        bizTypeListBean.add(homePageBean);

        BizTypeListVO.BizTypeBean indexBean = new BizTypeListVO.BizTypeBean();
        indexBean.setBizGroupName(BizGroupEnum.BIZ_HANDLE.getName());
        indexBean.setBizTypeList(indexList);
        bizTypeListBean.add(indexBean);

        BizTypeListVO.BizTypeBean quickAccessBean = new BizTypeListVO.BizTypeBean();
        quickAccessBean.setBizGroupName(BizGroupEnum.QUICK_APPLICATION.getName());
        quickAccessBean.setBizTypeList(quickAccessList);
        bizTypeListBean.add(quickAccessBean);

        BizTypeListVO.BizTypeBean daosanjiaoBean = new BizTypeListVO.BizTypeBean();
        daosanjiaoBean.setBizGroupName(BizGroupEnum.DAOSANJIAO_SUPPORT.getName());
        daosanjiaoBean.setBizTypeList(daosanjiaoSupportBizList);
        bizTypeListBean.add(daosanjiaoBean);

        BizTypeListVO bizTypeListVO = new BizTypeListVO();
        bizTypeListVO.setBizTypeListBean(bizTypeListBean);

        return ApiResult.of(0,bizTypeListVO);
    }

    @PostMapping("/configHomePageBiz")
    @SmIgnore
    public ApiResult<Void> configHomePageBiz(@RequestBody ConfigHomePageBizRequest request) {
        List<Long> bizIdList = request.getIds();
        if (bizIdList == null || bizIdList.isEmpty()) {
            throw new ApiException(StallUpErrorCodes.PARAM_ERROR);
        }
        bizIdList.forEach(i -> {
            if (!stallUpConfig.getConfig().getMap().containsKey(i)) {
                throw new ApiException(StallUpErrorCodes.PARAM_ERROR);
            }
        });
        return stallUpService.updateHomePageGridBiz(SmartGridContext.getUid(), bizIdList);
    }

    /**
     * 拼装扫楼场外营销数量
     * 
     * @param smartGridVOS
     * @param gridUserRoleDetail
     * @param outsideMarketingNumVO
     */
    private void buildSweepFloorCount(List<SmartGridVO> smartGridVOS, GridUserRoleDetail gridUserRoleDetail,
            OutsideMarketingNumVO outsideMarketingNumVO) {
        int sweepFloorNotStartCount = 0;
        int sweepFloorStartedCount = 0;
        int sweepFloorEndCount = 0;
        Date dayStartTime = DateUtils.getDayStartTime();
        Date dayEndTime = DateUtils.getDayEndTime();
        for (SmartGridVO smartGridVO : smartGridVOS) {
            if (gridUserRoleDetail.getId().equals(smartGridVO.getId())) {
                List<SweepFloorActivityVO> activityVOList = smartGridVO.getActivityVOList();
                if (!CollectionUtils.isEmpty(activityVOList)) {
                    for (SweepFloorActivityVO activityVO : activityVOList) {
                        if (activityVO.getGridId().equals(gridUserRoleDetail.getId())) {
                            if (SweepFloorStatusEnum.NOT_START.getId() == activityVO.getStatus()) {
                                if (dayStartTime.before(activityVO.getGmtCreate())
                                        && dayEndTime.after(activityVO.getGmtCreate())) {
                                    sweepFloorNotStartCount++;
                                }
                            } else if (SweepFloorStatusEnum.PROCESSING.getId() == activityVO.getStatus()) {
                                sweepFloorStartedCount++;
                            } else if (SweepFloorStatusEnum.END.getId() == activityVO.getStatus()
                                    || SweepFloorStatusEnum.ABNORMAL_END.getId() == activityVO.getStatus()
                                    || SweepFloorStatusEnum.CANCEL.getId() == activityVO.getStatus()) {
                                if (activityVO.getEndTime() != null && dayStartTime.before(activityVO.getEndTime())
                                        && dayEndTime.after(activityVO.getEndTime())) {
                                    sweepFloorEndCount++;
                                }
                            }
                        }
                    }

                }
            }
        }
        outsideMarketingNumVO.setSweepFloorStartedCount(sweepFloorStartedCount);
        outsideMarketingNumVO.setSweepFloorNotStartCount(sweepFloorNotStartCount);
        outsideMarketingNumVO.setSweepFloorEndCount(sweepFloorEndCount);
    }

    private StallUpBizType toBizTypeVO(StallUpBizType v) {
        StallUpBizType stallUpBizType = new StallUpBizType();
        stallUpBizType.setId(v.getId());
        stallUpBizType.setName(v.getName());
        stallUpBizType.setIsDisplay(v.getIsDisplay());
        stallUpBizType.setType(v.getType());
        stallUpBizType.setIcon(v.getIcon());
        stallUpBizType.setCategory(v.getCategory());
        stallUpBizType.setGroupId(v.getGroupId());
        if (v.getType() == 1) {
            stallUpBizType.setUrl(v.getUrl());
        }
        return stallUpBizType;
    }

    @Setter
    @Getter
    public static class ConfigHomePageBizRequest {
        private List<Long> ids;
    }

}
