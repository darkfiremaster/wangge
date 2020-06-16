package com.shinemo.wangge.core.service.targetcustomer;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.targetcustomer.domain.model.TargetIndexMobileDO;
import com.shinemo.targetcustomer.domain.response.TargetCustomerResponse;

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
    ApiResult<TargetCustomerResponse> findByMobile(String mobile);
}
