package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SweepStreetMerchantVO extends BaseDO {
    private String merchantsId;

    private String merchantsName;

    private String merchantsAddress;

    private String creatorMobile;

    private String contactName;

    private String contactMobile;

    private Boolean hasBroadband;

    private Long broadbandExpireTime;

    private String location;

    private Long visitTime;

    private String distance;

}
