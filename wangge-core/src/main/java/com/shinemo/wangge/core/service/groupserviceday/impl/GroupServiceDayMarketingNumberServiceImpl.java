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
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.model.SweepFloorBizDetail;
import com.shinemo.sweepvillage.domain.request.SweepVillageBusinessRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageBizDetail;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayMarketingNumberService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMarketingNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service("groupServiceDayMarketingNumberService")
public class GroupServiceDayMarketingNumberServiceImpl implements GroupServiceDayMarketingNumberService {


    @Resource
    private GroupServiceDayMarketingNumberMapper groupServiceDayMarketingNumberMapper;

    @Autowired
    private ThirdApiMappingV2Service thirdApiMappingV2Service;

    @Resource
    private StallUpConfig stallUpConfig;

    @Override
    public ApiResult<GroupServiceDayMarketNumberVO> getByActivityId(Long activityId) {
        Assert.notNull(activityId,"activityId is null");

        GroupServiceDayMarketingNumberQuery query = new GroupServiceDayMarketingNumberQuery();
        query.setGroupServiceDayId(activityId);

        GroupServiceDayMarketingNumberDO marketingNumberDO = groupServiceDayMarketingNumberMapper.get(query);

        GroupServiceDayMarketNumberVO numberVO = new GroupServiceDayMarketNumberVO();
        if (marketingNumberDO == null) {
            List<StallUpBizType> groupServiceBizList = stallUpConfig.getConfig().getPublicGroupServiceDayBizDataList();
            List<GroupServiceDayBizDetailVO> details = new ArrayList<>();
            for (StallUpBizType bizType : groupServiceBizList) {
                GroupServiceDayBizDetailVO bizDetail = new GroupServiceDayBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }
            numberVO.setPublicBizInfoList(details);

            List<StallUpBizType> informationGroupServiceDayBizDataList = stallUpConfig.getConfig().getInformationGroupServiceDayBizDataList();
            List<GroupServiceDayBizDetailVO> informationDetails = new ArrayList<>();
            for (StallUpBizType bizType : informationGroupServiceDayBizDataList) {
                GroupServiceDayBizDetailVO bizDetail = new GroupServiceDayBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                informationDetails.add(bizDetail);
            }
            numberVO.setInformationBizInfoList(informationDetails);
            return ApiResult.of(0, numberVO);
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
        int count = 0;
        if(publicBizList != null){
            count = publicBizList.stream().mapToInt(p -> p.getNum()).sum();
        }
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

        //若未传入办理量 默认填充0值
        if(CollectionUtils.isEmpty(request.getPublicBizList())){
            marketingNumberDO.setCount(0);
            List<StallUpBizType> groupServiceDayBizDataList = stallUpConfig.getConfig().getPublicGroupServiceDayBizDataList();
            List<GroupServiceDayBizDetailVO> details = new ArrayList<>();
            for (StallUpBizType bizType : groupServiceDayBizDataList) {
                GroupServiceDayBizDetailVO bizDetail = new GroupServiceDayBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }
            detail.setPublicBizInfoList(details);

            //政企
            List<StallUpBizType> informationGroupServiceDayBizDataList = stallUpConfig.getConfig().getInformationGroupServiceDayBizDataList();
            List<GroupServiceDayBizDetailVO> informationList = new ArrayList<>();
            for (StallUpBizType bizType : informationGroupServiceDayBizDataList) {
                GroupServiceDayBizDetailVO bizDetail = new GroupServiceDayBizDetailVO();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                informationList.add(bizDetail);
            }
            detail.setInformationBizInfoList(informationList);

            marketingNumberDO.setDetail(GsonUtils.toJson(detail));
        }


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
        publicBiz.setBizRemark(marketingNumberDO.getPublicBizRemark());
        publicBiz.setBizInfoList(transformationToHuawei(detailNew.getPublicBizInfoList()));
        bizList.add(publicBiz);

        if(request.getInformationBizList() != null){
            HuaweiGroupServiceDayBiz informationBiz = new HuaweiGroupServiceDayBiz();
            informationBiz.setBizTypeId(HuaweiBizTypeEnum.INFORMATION.getId());
            informationBiz.setBizRemark(marketingNumberDO.getInformationBizRemark());
            informationBiz.setBizInfoList(transformationToHuawei(detailNew.getInformationBizInfoList()));
            bizList.add(informationBiz);
        }

        map.put("activityId", GroupServiceDayConstants.ID_PREFIX + marketingNumberDO.getGroupServiceDayId());
//        map.put("mobile", SmartGridContext.getMobile());
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
