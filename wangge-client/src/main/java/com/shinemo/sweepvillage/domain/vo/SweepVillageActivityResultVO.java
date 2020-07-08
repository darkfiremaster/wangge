package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:47
 * @Desc
 */
@Data
public class SweepVillageActivityResultVO {

    /** 扫村活动ID  */
    private Long sweepVillageActivityId;

    /** 扫村活动标题 */
    private String title;

    /** 村庄ID */
    private String villageId ;

    /** 村庄名称 */
    private String villageName;

    /**
     * 打卡详细地址
     */
    private String address;

    /** 地区 */
    private String area;

    /** 状态 0:未开始 1:进行中 2:已结束 */
    private Integer status;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 办理量总计 */
    private Integer handleCount;

    /** 走访户数 */
    private Integer visitCount;
}
