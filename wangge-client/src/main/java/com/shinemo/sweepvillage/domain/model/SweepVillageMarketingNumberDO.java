package com.shinemo.sweepvillage.domain.model;

import lombok.Data;

import java.util.Date;

/**
 * 类说明:扫村办理量DO
 *
 * @author zengpeng
 */
@Data
public class SweepVillageMarketingNumberDO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String mobile;

    /**
     * 扫村活动ID
     */
    private Long activityId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 总数量
     */
    private Integer count;

    /**
     * 详情
     */
    private String detail;

    /** 营销人员id */
    private String userId;
}
