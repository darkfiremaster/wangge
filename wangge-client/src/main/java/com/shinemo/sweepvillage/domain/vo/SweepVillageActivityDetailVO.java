package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 类说明: 扫村活动详情
 *
 * @author zengpeng
 */
@Data
public class SweepVillageActivityDetailVO {
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


    /** 状态 0:未开始 1:进行中 2:已结束 */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;


    /** 走访户数 */
    private List<SweepVillageBizDetail> bizList;

    /**
     * 创建人名称
     */
    private String creatorName;
}
