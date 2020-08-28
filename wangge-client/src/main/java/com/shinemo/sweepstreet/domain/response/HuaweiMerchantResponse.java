package com.shinemo.sweepstreet.domain.response;

import com.shinemo.client.common.BaseDO;
import lombok.Data;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
public class HuaweiMerchantResponse extends BaseDO {
    private String merchantsId;

    private String groupName;

    private String groupAddress;

    private String creatorMobile;

    private String contactPerson;

    private String contactMobile;

    private Boolean hasBroadband;

    private String broadbandExpireTime;

//    private String location;
    private String latitude;
    private String longitude;

    private String visitTime;

    private String distance;
}
