package com.shinemo.wangge.core.service.groupserviceday.impl;

import com.google.gson.reflect.TypeToken;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayMarketNumberDetail;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayMarketingNumberDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayMarketingNumberQuery;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayMarketNumberVO;
import com.shinemo.wangge.core.service.groupserviceday.GroupServiceDayMarketingNumberService;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMarketingNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

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
}
