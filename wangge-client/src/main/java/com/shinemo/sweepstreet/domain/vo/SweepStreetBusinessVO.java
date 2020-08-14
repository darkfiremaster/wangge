package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * 类说明: 商户业务详情
 */
@Data
public class SweepStreetBusinessVO extends BaseDO {


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
}
