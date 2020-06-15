package com.shinemo.wangge.core.service.targetcommunity.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.model.TargetIndexMobileDO;
import com.shinemo.targetcustomer.domain.query.TargetIndexMobileQuery;
import com.shinemo.wangge.core.service.targetcommunity.TargetIndexMobileService;
import com.shinemo.wangge.dal.mapper.TargetIndexMobileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public ApiResult<List<TargetIndexMobileDO>> findByMobile(String mobile) {
        Assert.hasText(mobile,"mobile is null");

        TargetIndexMobileQuery query = new TargetIndexMobileQuery();
        query.setMobile(mobile);
        query.setPageEnable(false);

        List<TargetIndexMobileDO> targetIndexMobileDOS = targetIndexMobileMapper.find(query);
        if(CollectionUtils.isEmpty(targetIndexMobileDOS)){
            log.error("[findByMobile] error,mobile:{}",mobile);
            return ApiResult.of(0);
        }
        return ApiResult.success(targetIndexMobileDOS);
    }
}
