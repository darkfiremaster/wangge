package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

/**
 * 华为返回的住户信息
 */
@Data
public class HuaweiHouseResponse extends CommonHuaweiResponse {
    /** 住户id */
    private String houseId;
    /** 住户门牌号 */
    private String houseNumber;
    /** 运营商 */
    private String serviceProvider;
    /** 宽带到期时间 */
    private String broadbandExpireTime;
    /** 是否有家庭宽带 */
    private Integer broadbandFlag;
    /** 标签 */
    private String houseLabel;
    /** 家庭成员集合 */
    private List<HuaweiContactResponse> concactList;
    /** 住房类型 */
    private String roomType;
    /** 宽带备注 */
    private String broadbandRemark;
    /** 宽带月租 */
    private Double broadbandMonthlyrent;
    /** 电视盒类型 */
    private String TVBoxTypes;
    /** 电视盒备注 */
    private String TVBoxRemark;
    /** 电视盒过期时间 */
    private String TVBoxExpireTime;
}
