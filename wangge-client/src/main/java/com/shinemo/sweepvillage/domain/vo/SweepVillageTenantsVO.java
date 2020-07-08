package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SweepVillageTenantsVO {
    private String id;
    /** 住户id */
    private String tenantsId;
    /** 扫村计划id */
    private Long activityId;
    /** 村庄id */
    private String villageId;
    /** 地区 */
    private String area;
    /** 地区code */
    private String areaCode;
    /** 详细地址 */
    private String address;
    /** 坐标 */
    private String location;
    /** 地址备注 */
    private String addressRemark;
    /** 图片链接 */
    private List<String> pictureUrls;
    /** 宽带类型 */
    private List<String> broadbandTypes;
    /** 宽带地址 */
    private String broadbandRemark;
    /** 宽带到期时间 */
    private Date broadbandExpireTime;
    /** 电视盒类型 */
    private List<String> TVBoxTypes;
    /** 电视盒备注 */
    private String TVBoxRemark;
    /** 电视盒到期时间 */
    private Date TVBoxExpireTime;
    /** 关联箱体 */
    private String associatedCabinet;
    /** 端口号 */
    private String port;
    /** 家庭联系人 */
    private List<SweepVillageFamilyMember> contactPersonList;
    /** 村庄名 */
    private String villageName;
    /** 联系人姓名 */
    private String contactPerson;
    /** 联系人手机号 */
    private String contactMobile;
    /** 住户搜索时的联系人手机号 */
    private String mobile;
    /** 最近走访时间 */
    private Date visitTime;
    /** 距离 */
    private String distance;
}
