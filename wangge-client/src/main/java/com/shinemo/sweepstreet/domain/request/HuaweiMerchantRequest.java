package com.shinemo.sweepstreet.domain.request;

import com.shinemo.client.common.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 类说明: 华为接口 商户列表
 *
 * @author zengpeng
 */
@Data
@Builder
public class HuaweiMerchantRequest extends BaseDO {

    private String queryParam;
    private String location;
    private Integer pageSize;
    private Integer currentPage;
    private Boolean pageFlag;

    /** 传空表示不按范围筛选 */
    private String radius;
}
