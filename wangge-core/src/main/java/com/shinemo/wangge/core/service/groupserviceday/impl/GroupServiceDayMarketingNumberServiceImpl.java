package com.shinemo.wangge.core.service.groupserviceday.impl;

import com.google.gson.reflect.TypeToken;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.constant.GroupServiceDayConstants;
import com.shinemo.groupserviceday.domain.enums.HuaweiBizTypeEnum;
import com.shinemo.groupserviceday.domain.enums.HuaweiGroupServiceDayUrlEnum;
import com.shinemo.groupserviceday.domain.model.*;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayMarketingNumberQuery;
import com.shinemo.groupserviceday.domain.request.GroupServiceDayBusinessRequest;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBizDetailVO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayMarketNumberVO;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.sweepvillage.domain.enums.HuaweiSweepVillageUrlEnum;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayMarketingNumberService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMarketingNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
@Service("groupServiceDayMarketingNumberService")
public class GroupServiceDayMarketingNumberServiceImpl implements GroupServiceDayMarketingNumberService {


    @Resource
    private GroupServiceDayMarketingNumberMapper groupServiceDayMarketingNumberMapper;

    @Autowired
    private ThirdApiMappingV2Service thirdApiMappingV2Service;

    @Override
    public ApiResult<GroupServiceDayMarketNumberVO> getByActivityId(Long activityId) {
        Assert.notNull(activityId,"activityId is null");

        GroupServiceDayMarketingNumberQuery query = new GroupServiceDayMarketingNumberQuery();
        query.setGroupServiceDayId(activityId);

        GroupServiceDayMarketingNumberDO marketingNumberDO = groupServiceDayMarketingNumberMapper.get(query);
        if(marketingNumberDO == null){
            log.error("[getByActivityId] is null,query:{}",query);
            return ApiResult.fail("marketNumber is null",500);
        }


        GroupServiceDayMarketNumberDetail detail = GsonUtil.fromGson2Obj(marketingNumberDO.getDetail(),
                new TypeToken<GroupServiceDayMarketNumberDetail>() {}.getType());
        GroupServiceDayMarketNumberVO build = GroupServiceDayMarketNumberVO.builder()
                .publicBizInfoList(detail.getPublicBizInfoList())
                .publicBizRemark(marketingNumberDO.getPublicBizRemark())
                .informationBizInfoList(detail.getInformationBizInfoList())
                .informationBizRemark(marketingNumberDO.getInformationBizRemark())
                .build();

        return ApiResult.success(build);
    }

    @Override
    public ApiResult enterMarketingNumber(GroupServiceDayBusinessRequest request) {
        //1.统计办理量
        List<GroupServiceDayBizDetailVO> informationBizList = request.getInformationBizList();
        List<GroupServiceDayBizDetailVO> publicBizList = request.getPublicBizList();
        int count = publicBizList.stream().mapToInt(p -> p.getNum()).sum();
        if(informationBizList != null){
            count = count + informationBizList.stream().mapToInt(p -> p.getNum()).sum();
        }

        //2.判断是否存在记录
        GroupServiceDayMarketingNumberQuery query = new GroupServiceDayMarketingNumberQuery();
        query.setGroupServiceDayId(request.getActivityId());
        GroupServiceDayMarketingNumberDO queryResult = groupServiceDayMarketingNumberMapper.get(query);

        //3.插入或更新
        GroupServiceDayMarketNumberDetail detail = GroupServiceDayMarketNumberDetail.builder()
                .publicBizInfoList(request.getPublicBizList())
                .informationBizInfoList(request.getInformationBizList())
                .build();
        GroupServiceDayMarketingNumberDO marketingNumberDO = GroupServiceDayMarketingNumberDO.builder()
                .groupServiceDayId(request.getActivityId())
                .userId(SmartGridContext.getUid())
                .detail(GsonUtil.toJson(detail))
                .publicBizRemark(request.getPublicBizRemark())
                .informationBizRemark(request.getInformationRemark())
                .count(count)
                .build();

        if(queryResult == null){
            //插入
            log.info("[enterMarketingNumber] insert marketingNumberDO:{}",marketingNumberDO);
            groupServiceDayMarketingNumberMapper.insert(marketingNumberDO);
        }else {
            marketingNumberDO.setId(queryResult.getId());
            log.info("[enterMarketingNumber] update marketingNumberDO:{}",marketingNumberDO);
            groupServiceDayMarketingNumberMapper.update(marketingNumberDO);
        }
        //4.同步给华为
        Map<String, Object> map = new HashMap<>(16);
        GroupServiceDayMarketNumberDetail detailNew = GsonUtil.fromGson2Obj(marketingNumberDO.getDetail(),
                new TypeToken<GroupServiceDayMarketNumberDetail>() {}.getType());

        List<HuaweiGroupServiceDayBiz> bizList = new ArrayList<>();
        HuaweiGroupServiceDayBiz publicBiz = new HuaweiGroupServiceDayBiz();
        publicBiz.setBizTypeId(HuaweiBizTypeEnum.PUBLIC.getId());
        publicBiz.setInformationBizRemark(marketingNumberDO.getPublicBizRemark());
        publicBiz.setPublicBizInfoList(transformationToHuawei(detailNew.getPublicBizInfoList()));
        bizList.add(publicBiz);

        if(request.getInformationBizList() != null){
            HuaweiGroupServiceDayBiz informationBiz = new HuaweiGroupServiceDayBiz();
            informationBiz.setBizTypeId(HuaweiBizTypeEnum.INFORMATION.getId());
            informationBiz.setInformationBizRemark(marketingNumberDO.getInformationBizRemark());
            informationBiz.setPublicBizInfoList(transformationToHuawei(detailNew.getInformationBizInfoList()));
            bizList.add(informationBiz);
        }

        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + marketingNumberDO.getGroupServiceDayId());
        map.put("mobile", SmartGridContext.getMobile());
        map.put("bizTypeList", bizList);

        thirdApiMappingV2Service.asyncDispatch(map, HuaweiGroupServiceDayUrlEnum.ADD_OR_UPDATE_GROUP_SERVICE_DAY_DATA.getApiName(), SmartGridContext.getMobile());

        return ApiResult.success();
    }


    public List<HuaweiGroupServiceDayBizDetail> transformationToHuawei(List<GroupServiceDayBizDetailVO> bizDetailVOS){
        List<HuaweiGroupServiceDayBizDetail> bizDetails = new ArrayList<>();

        for(GroupServiceDayBizDetailVO bizDetailVO : bizDetailVOS){
            bizDetails.add(HuaweiGroupServiceDayBizDetail.builder()
                    .bizId(bizDetailVO.getId()+"")
                    .bizName(bizDetailVO.getName())
                    .bizCount(bizDetailVO.getNum()+"")
                    .build());
        }

        return bizDetails;
    }
}
