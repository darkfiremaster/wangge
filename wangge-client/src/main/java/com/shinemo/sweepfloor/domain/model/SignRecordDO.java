package com.shinemo.sweepfloor.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class SignRecordDO {
    private Long id;
    /** 用户id */
    private String userId;
    /** 活动Id */
    private Long activityId;
    /** 开始打卡时间 */
    private Date startTime;
    /** 结束打卡时间 */
    private Date endTime;
    /** 业务类型 */
    private Integer bizType;
    /** 开始打卡地址 */
    private String startLocation;
    /** 结束打卡地址 */
    private String endLocation;
    /** 图片地址 */
    private String imgUrl;
    private Date gmtCreate;
    private Date gmtModified;
    /** 备注 */
    private String remark;
}
