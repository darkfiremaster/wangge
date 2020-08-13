package com.shinemo.sweepstreet.domain.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.shinemo.client.common.BaseDO;


/**
 * 实体类
 * @ClassName: SweepStreetMarketingNumberDO
 * @author Zeng Peng
 * @Date 2020-08-13 15:35:21
 */
@Getter
@Setter
public class SweepStreetMarketingNumberDO extends BaseDO {
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private String userId;
	/**
	* 子扫街活动id
	*/
	private Long sweepStreetId;
	/**
	* 办理量
	*/
	private Integer count;
	/**
	* 办理量详情，json
	*/
	private String detail;
	/**
	* 业务统计备注
	*/
	private String bizRemark;
}
