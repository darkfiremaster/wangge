package com.shinemo.sweepvillage.domain.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:24
 * @Desc
 */
@Data
public class SweepVillageActivityDO {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    /**
     * 标题
     */
    private String title;
    /**
     * 村庄id
     */
    private String villageId;
    /**
     * 村庄名称
     */
    private String villageName;
    /**
     * 地区
     */
    private String area;
    /**
     * 地区code
     */
    private String areaCode;
    /**
     * 打卡人的经纬度坐标
     */
    private String location;
    /**
     * 转化后的打卡人的经纬度坐标
     */
    private String rgsLocation;

    /**
     * 网格id
     */
    private String gridId;
    /**
     * 创建人手机号
     */
    private String mobile;
    /**
     * 活动状态
     * @see com.shinemo.sweepvillage.enums.SweepVillageStatusEnum
     */
    private Integer status;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;

}
