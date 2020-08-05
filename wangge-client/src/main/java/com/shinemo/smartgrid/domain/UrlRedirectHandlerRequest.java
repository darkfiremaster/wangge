package com.shinemo.smartgrid.domain;

import com.shinemo.stallup.domain.params.BizParams;
import lombok.Data;

/**
 * url参数拼接请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-15
 */
@Data
public class UrlRedirectHandlerRequest {
	/**
	 * 业务id
	 */
	private Long id;
	/**
	 * 用户手机
	 */
	private String userPhone;
	/**
	 * 查询手机
	 */
	private String queryMobile;
	/**
	 * 业务参数
	 */
	private BizParams bizParams;
	/**
	 * url
	 */
	private String url;
	/**
	 * 小区id
	 */
	private String communityId;
	/** 任务id */
	private Long activityId;
	/** 小区名 */
	private String communityName;

	/**
	 * 活动Id,由前端拼接活动前缀,用于码店业务办理跳转
	 */
	private String activityStrId;

	public <T extends BizParams> T getBizParams() {
		if (bizParams != null) {
			return (T) bizParams;
		} else {
			return null;
		}
	}
}
