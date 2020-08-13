package com.shinemo.sweepstreet.domain.query;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.shinemo.client.common.QueryBase;

/**
 * 查询类
 * @ClassName: SweepStreetVisitRecordingQuery
 * @author Zeng Peng
 * @Date 2020-08-13 15:35:20
 */
@Getter
@Setter
public class SweepStreetVisitRecordingQuery extends QueryBase {
	private Long id;
    /**
    * 子扫街活动id
    */
	private Long activityId;
    /**
    * 营销人员手机号
    */
	private String mobile;
    /**
    * 营销人员名
    */
	private String marketingUserName;
    /**
    * 商户id
    */
	private String merchantId;
    /**
    * 联系人姓名
    */
	private String contactName;
    /**
    * 联系人手机
    */
	private String contactMobile;
    /**
    * 是否投诉敏感客户，0不投诉，1投诉
    */
	private Integer complaintSensitiveCustomersFlag;
    /**
    * 营销是否成功，0不成功，1成功
    */
	private Integer successFlag;
    /**
    * 办理业务，字符串存储
    */
	private String businessType;
    /**
    * 家庭宽带
    */
	private String homeBroadband;
    /**
    * 宽带月租
    */
	private String broadbandMonthlyRent;
    /**
    * 宽带备注
    */
	private String broadbandRemark;
    /**
    * 宽带到期时间
    */
	private Date broadbandExpireTime;
    /**
    * 家庭电视
    */
	private String homeTvBox;
    /**
    * 电视月租
    */
	private String tvBoxMonthlyRent;
    /**
    * 电视备注
    */
	private String tvBoxRemark;
    /**
    * 电视到期时间
    */
	private Date tvBoxExpireTime;
    /**
    * 备注
    */
	private String remark;
    /**
    * 创建时间
    */
	private Date gmtCreate;
    /**
    * 更新时间
    */
	private Date gmtModified;
    /**
    * 状态，1正常，0删除
    */
	private Integer status;
    /**
    * 经纬度
    */
	private String location;
    /**
    * 商户创建人手机号
    */
	private String createMerchantMobile;
}
