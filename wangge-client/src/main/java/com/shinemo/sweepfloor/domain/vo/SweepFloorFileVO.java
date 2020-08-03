package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 文件接口同步扫楼数据
 */
@Data
public class SweepFloorFileVO {
    /** 扫楼创建日期 */
    private Date doorDate;
    /** 小区id */
    private String buildingId;
    /** 小区名 */
    private String buildingName;
    /** 详细地址 */
    private String detaiAddr;
    /** 网格id */
    private String areaId;
    /** 扫楼id */
    private String doorId;
    /** 扫楼名 */
    private String doorName;
    /** 创建时间 */
    private Date createTime;
    /** 扫楼参与人数 */
    private Integer joinManCnt;
    /** 扫楼楼栋数 */
    private Integer doorDoorCnt;

}
