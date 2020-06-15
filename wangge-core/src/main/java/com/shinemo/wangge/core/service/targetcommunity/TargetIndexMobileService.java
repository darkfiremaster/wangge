package com.shinemo.wangge.core.service.targetcommunity;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.model.TargetIndexMobileDO;

import java.util.List;

/**
 * 目标客户->手机->指标
 */
public interface TargetIndexMobileService {

    /**
     * 通过手机查询指标
     * @param mobile
     * @return
     */
    ApiResult<List<TargetIndexMobileDO>> findByMobile(String mobile);
}
