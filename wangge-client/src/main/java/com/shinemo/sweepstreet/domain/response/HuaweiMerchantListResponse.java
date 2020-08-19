package com.shinemo.sweepstreet.domain.response;

import com.shinemo.client.common.BaseDO;
import lombok.Data;

import java.util.List;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
public class HuaweiMerchantListResponse extends BaseDO {
    private List<HuaweiMerchantResponse> groupList;
}
