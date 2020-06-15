package com.shinemo.wangge.core.service.targetcommunity.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.model.TargetIndexCommunityDO;
import com.shinemo.targetcustomer.domain.query.TargetIndexCommunityQuery;
import com.shinemo.wangge.core.service.targetcommunity.TargetIndexCommunityService;
import com.shinemo.wangge.dal.mapper.TargetIndexCommunityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 类说明:
 *
 * @author 曾鹏
 */
@Slf4j
@Service("targetIndexCommunityService")
public class TargetIndexCommunityServiceImpl implements TargetIndexCommunityService {

    @Resource
    private TargetIndexCommunityMapper targetIndexCommunityMapper;

    @Override
    public ApiResult<List<TargetIndexCommunityDO>> findByIndexId(Long indexId) {
        Assert.notNull(indexId,"indexId is null");

        TargetIndexCommunityQuery query = new TargetIndexCommunityQuery();
        query.setIndexId(indexId);

        query.setOrderByEnable(true);
        query.putOrderBy("upper_limit",true);
        query.putOrderBy("lower_limit",true);
        query.setPageEnable(false);

        List<TargetIndexCommunityDO> targetIndexCommunityDOS = targetIndexCommunityMapper.find(query);
        if(CollectionUtils.isEmpty(targetIndexCommunityDOS)){
            log.error("[findByIndexId] error,indexId:{}",indexId);
            return ApiResult.of(0);
        }
        return ApiResult.success(targetIndexCommunityDOS);
    }
}
