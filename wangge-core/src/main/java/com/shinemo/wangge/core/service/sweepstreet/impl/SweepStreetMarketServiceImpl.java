package com.shinemo.wangge.core.service.sweepstreet.impl;

import com.google.gson.reflect.TypeToken;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.constant.GroupServiceDayConstants;
import com.shinemo.groupserviceday.domain.enums.HuaweiBizTypeEnum;
import com.shinemo.groupserviceday.domain.enums.HuaweiGroupServiceDayUrlEnum;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.sweepstreet.domain.contants.SweepStreetActivityConstants;
import com.shinemo.sweepstreet.domain.model.HuaweiSweepStreetBiz;
import com.shinemo.sweepstreet.domain.model.HuaweiSweepStreetBizDetail;
import com.shinemo.sweepstreet.domain.model.SweepStreetMarketingNumberDO;
import com.shinemo.sweepstreet.domain.query.SweepStreetMarketingNumberQuery;
import com.shinemo.sweepstreet.domain.request.SweepStreetBusinessRequest;
import com.shinemo.sweepstreet.domain.vo.SweepStreetBizDetailVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMarketNumberVO;
import com.shinemo.sweepstreet.enums.HuaweiSweepStreetActivityUrlEnum;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetMarketService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.dal.mapper.SweepStreetMarketingNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Slf4j
@Service("sweepStreetMarketService")
public class SweepStreetMarketServiceImpl implements SweepStreetMarketService {

    @Resource
    private SweepStreetMarketingNumberMapper sweepStreetMarketingNumberMapper;

    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private ThirdApiMappingV2Service thirdApiMappingV2Service;

    @Override
    public ApiResult<SweepStreetMarketNumberVO> getByActivityId(Long activityId) {
        Assert.notNull(activityId,"activityId is null");

        SweepStreetMarketingNumberQuery query = new SweepStreetMarketingNumberQuery();
        query.setSweepStreetId(activityId);

        //若不存在记录，返回默认值
        SweepStreetMarketingNumberDO sweepStreetMarketingNumberDO = sweepStreetMarketingNumberMapper.get(query);

        if(sweepStreetMarketingNumberDO == null){
            List<StallUpBizType> sweepStreetBizList = stallUpConfig.getConfig().getSweepStreetBizDataList();
            List<SweepStreetBizDetailVO> details = new ArrayList<>();
            for (StallUpBizType bizType : sweepStreetBizList) {
                SweepStreetBizDetailVO bizDetail = new SweepStreetBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }

            return ApiResult.of(0, SweepStreetMarketNumberVO.builder()
                    .bizInfoList(details)
                    .build());
        }
        SweepStreetMarketNumberVO sweepStreetMarketNumberVO = new SweepStreetMarketNumberVO();
        List<SweepStreetBizDetailVO> details = GsonUtil.fromGson2Obj(sweepStreetMarketingNumberDO.getDetail(),
                new TypeToken<List<SweepStreetBizDetailVO>>() {}.getType());
        sweepStreetMarketNumberVO.setBizInfoList(details);
        sweepStreetMarketNumberVO.setBizRemark(sweepStreetMarketingNumberDO.getBizRemark());

        return ApiResult.of(0,sweepStreetMarketNumberVO);
    }

    @Override
    public ApiResult enterMarketingNumber(SweepStreetBusinessRequest request) {
        //1.统计办理量
        List<SweepStreetBizDetailVO> bizList = request.getBizList();
        int count = bizList.stream().mapToInt(p -> p.getNum()).sum();

        //2.判断是否存在记录
        SweepStreetMarketingNumberQuery query = new SweepStreetMarketingNumberQuery();
        query.setSweepStreetId(request.getActivityId());
        SweepStreetMarketingNumberDO queryResult = sweepStreetMarketingNumberMapper.get(query);

        //3.插入或更新

        SweepStreetMarketingNumberDO marketingNumberDO = SweepStreetMarketingNumberDO.builder()
                .sweepStreetId(request.getActivityId())
                .userId(SmartGridContext.getUid())
                .detail(GsonUtil.toJson(request.getBizList()))
                .bizRemark(request.getRemark())
                .count(count)
                .build();

        //若未传入办理量 默认填充0值
        if(CollectionUtils.isEmpty(request.getBizList())){
            marketingNumberDO.setCount(0);
            List<StallUpBizType> sweepStreetBizList = stallUpConfig.getConfig().getSweepStreetBizDataList();
            List<SweepStreetBizDetailVO> details = new ArrayList<>();
            for (StallUpBizType bizType : sweepStreetBizList) {
                SweepStreetBizDetailVO bizDetail = new SweepStreetBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }
            marketingNumberDO.setDetail(GsonUtils.toJson(details));
        }


        if(queryResult == null){
            //插入
            log.info("[enterMarketingNumber] insert sweep street marketingNumberDO:{}",marketingNumberDO);
            sweepStreetMarketingNumberMapper.insert(marketingNumberDO);
        }else {
            marketingNumberDO.setId(queryResult.getId());
            log.info("[enterMarketingNumber] update sweep street  marketingNumberDO:{}",marketingNumberDO);
            sweepStreetMarketingNumberMapper.update(marketingNumberDO);
        }
        //4.同步给华为
        Map<String, Object> map = new HashMap<>(16);
        List<SweepStreetBizDetailVO> detailsNew = GsonUtil.fromGson2Obj(marketingNumberDO.getDetail(),
                new TypeToken<List<SweepStreetBizDetailVO>>() {}.getType());


        List<HuaweiSweepStreetBiz> sweepStreetBizs = new ArrayList<>();
        HuaweiSweepStreetBiz sweepStreetBiz = new HuaweiSweepStreetBiz();
        sweepStreetBiz.setBizRemark(marketingNumberDO.getBizRemark());
        sweepStreetBiz.setBizInfoList(transformationToHuawei(detailsNew));
        sweepStreetBiz.setBizTypeId(HuaweiBizTypeEnum.STREET_BIZ.getId());

        map.put("activityId", SweepStreetActivityConstants.ID_PREFIX + marketingNumberDO.getSweepStreetId());
        map.put("bizTypeList", sweepStreetBizs);

        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.ADD_OR_UPDATE_SWEEP_STREET_ACTIVITY_DATA.getApiName(), SmartGridContext.getMobile());

        return ApiResult.success();
    }


    public List<HuaweiSweepStreetBizDetail> transformationToHuawei(List<SweepStreetBizDetailVO> bizDetailVOS){
        List<HuaweiSweepStreetBizDetail> bizDetails = new ArrayList<>();

        for(SweepStreetBizDetailVO bizDetailVO : bizDetailVOS){
            bizDetails.add(HuaweiSweepStreetBizDetail.builder()
                    .bizId(bizDetailVO.getId()+"")
                    .bizName(bizDetailVO.getName())
                    .bizCount(bizDetailVO.getNum()+"")
                    .build());
        }

        return bizDetails;
    }
}
