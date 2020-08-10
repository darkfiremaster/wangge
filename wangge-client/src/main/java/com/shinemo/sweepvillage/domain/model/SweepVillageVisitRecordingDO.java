package com.shinemo.sweepvillage.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class SweepVillageVisitRecordingDO {
    private Long id;
    /** 扫村计划id */
    private Long activityId;
    /** 住户id */
    private String tenantsId;
    /** 添加人手机号 */
    private String mobile;
    /** 营销人员名 */
    private String marketingUserName;
    /** 联系人姓名 */
    private String contactName;
    /** 联系人手机号 */
    private String contactMobile;
    /** 是否投诉敏感客户 */
    private Integer complaintSensitiveCustomersFlag;
    /** 是否营销成功标识 */
    private Integer successFlag;
    /** 办理业务 */
    private String businessType;
    /** 宽带过期时间 */
    private Date broadbandExpireTime;
    /** 电视盒到期时间*/
    private Date tvBoxExpireTime;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private Date gmtCreate;
    /** 更新时间 */
    private Date gmtModified;
    /** 状态，1正常，0删除 */
    private Integer status;
    /** 经纬度 */
    private String location;
    /** 住户创建人手机号 */
    private String createTenantsMobile;
}
