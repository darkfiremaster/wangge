package com.shinemo.wangge.core.service.targetcustomer.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.model.TargetCustomersIndexDO;
import com.shinemo.targetcustomer.domain.model.TargetIndexCommunityDO;
import com.shinemo.targetcustomer.domain.model.TargetIndexMobileDO;
import com.shinemo.targetcustomer.domain.query.TargetCustomersIndexQuery;
import com.shinemo.targetcustomer.domain.query.TargetIndexCommunityQuery;
import com.shinemo.targetcustomer.domain.query.TargetIndexMobileQuery;
import com.shinemo.targetcustomer.domain.response.TargetCommunityResponse;
import com.shinemo.targetcustomer.domain.response.TargetCustomerResponse;
import com.shinemo.targetcustomer.domain.response.TargetIndexResponse;
import com.shinemo.wangge.core.service.targetcustomer.TargetIndexMobileService;
import com.shinemo.wangge.dal.mapper.TargetCustomersIndexMapper;
import com.shinemo.wangge.dal.mapper.TargetIndexCommunityMapper;
import com.shinemo.wangge.dal.mapper.TargetIndexMobileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 类说明:
 *
 * @author 曾鹏
 */
@Service
@Slf4j
public class TargetIndexMobileServiceImpl implements TargetIndexMobileService {

    @Resource
    private TargetIndexMobileMapper targetIndexMobileMapper;

    @Resource
    private TargetCustomersIndexMapper targetCustomersIndexMapper;

    @Resource
    private TargetIndexCommunityMapper targetIndexCommunityMapper;


    private final Integer COMMUNITY_SIZE = 5;

    @Override
    public ApiResult<TargetCustomerResponse> findByMobile(String mobile) {
        Assert.hasText(mobile, "mobile is null");

        TargetCustomerResponse targetCustomerResponse = new TargetCustomerResponse();
        //1.获取指标列表
        TargetIndexMobileQuery query = new TargetIndexMobileQuery();
        query.setMobile(mobile);
        query.setPageEnable(false);

        List<TargetIndexMobileDO> targetIndexMobileDOS = targetIndexMobileMapper.find(query);
        if (CollectionUtils.isEmpty(targetIndexMobileDOS)) {
            log.error("[findByMobile] error,mobile:{}", mobile);
            return ApiResult.of(0);
        }
        targetCustomerResponse.setMobile(mobile);


        List<TargetIndexResponse> targetIndexResponseList = new ArrayList<>(targetIndexMobileDOS.size());

        //去除mobile对应的重复的indexId
        Set<Long> indexIdSet = new HashSet<>();
        for(TargetIndexMobileDO mobileDO : targetIndexMobileDOS){
            indexIdSet.add(mobileDO.getIndexId());

        }

        for(TargetIndexMobileDO targetIndexMobileDO : targetIndexMobileDOS){
            if(!indexIdSet.contains(targetIndexMobileDO.getIndexId())){
                continue;
            }
            indexIdSet.remove(targetIndexMobileDO.getIndexId());
            TargetIndexResponse  targetIndexResponse = new TargetIndexResponse();
            targetIndexResponse.setDeadlineTime(targetIndexMobileDO.getDeadlineTime());
            targetIndexResponse.setIndexId(targetIndexMobileDO.getIndexId());

            //2.获取指标详情
            TargetCustomersIndexQuery indexQuery = new TargetCustomersIndexQuery();
            indexQuery.setId(targetIndexMobileDO.getIndexId());
            TargetCustomersIndexDO targetCustomersIndexDO = targetCustomersIndexMapper.get(indexQuery);
            if(targetCustomersIndexDO == null){
                log.error("[getByIndexId] index not exits,query:{}",query);
                continue;
            }

            //初始化targetIndexResponse
            targetIndexResponse.setDeadlineTime(targetIndexMobileDO.getDeadlineTime());
            targetIndexResponse.setIndexId(targetIndexMobileDO.getIndexId());
            targetIndexResponse.setIndexName(targetCustomersIndexDO.getName());
            targetIndexResponse.setIndexCode(targetCustomersIndexDO.getIndexCode());



            //3.获取指标下的小区详情
            TargetIndexCommunityQuery communityQuery = new TargetIndexCommunityQuery();
            communityQuery.setIndexId(targetIndexMobileDO.getIndexId());

            query.setOrderByEnable(true);
            query.putOrderBy("upper_limit",true);
            query.putOrderBy("lower_limit",true);
            query.setPageEnable(false);


            List<TargetIndexCommunityDO> targetIndexCommunityDOS = targetIndexCommunityMapper.find(communityQuery);
            if(CollectionUtils.isEmpty(targetIndexCommunityDOS)){
                log.error("[findByIndexId] error,indexId:{}",communityQuery.getIndexId());
                continue;
            }

            //初始化TargetCommunityResponseList
            List<TargetCommunityResponse> targetCommunityResponseList = new ArrayList<>(targetIndexCommunityDOS.size());

            int size = 0;
            for(TargetIndexCommunityDO targetIndexCommunityDO : targetIndexCommunityDOS){
                if(size >= COMMUNITY_SIZE){
                    continue;
                }
                TargetCommunityResponse targetCommunityResponse = new TargetCommunityResponse();
                targetCommunityResponse.setCommunityId(targetIndexCommunityDO.getCommunityId());
                targetCommunityResponse.setCommunityName(targetIndexCommunityDO.getCommunityName());
                targetCommunityResponse.setAddress(targetIndexCommunityDO.getAddress());
                targetCommunityResponse.setLocation(targetIndexCommunityDO.getLocation());
                targetCommunityResponse.setUpperLimit(targetIndexCommunityDO.getUpperLimit());
                targetCommunityResponse.setLowerLimit(targetIndexCommunityDO.getLowerLimit());
                targetCommunityResponseList.add(targetCommunityResponse);
                size++;
            }


            targetIndexResponse.setCommunityList(targetCommunityResponseList);
            targetIndexResponseList.add(targetIndexResponse);
        }

        targetCustomerResponse.setIndexList(targetIndexResponseList);
        return ApiResult.success(targetCustomerResponse);
    }
}
