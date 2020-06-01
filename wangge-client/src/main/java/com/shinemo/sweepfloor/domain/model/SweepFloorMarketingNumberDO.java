package com.shinemo.sweepfloor.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class SweepFloorMarketingNumberDO {
    private Long id;
    /** 营销人员id */
    private String userId;
    /** 扫楼活动id */
    private Long activityId;
    private Integer count;
    private String detail;
    /** 备注 */
    private String remark;
    private Date gmtCreate;
    private Date gmtModified;
}
