package com.shinemo.stallup.domain.request;

import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.model.StallUpActivity;
import lombok.Data;

/**
 * 摆摊请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-01
 */
@Data
public class StallUpRequest {
	/**
	 * 摆摊状态
	 * @see StallUpStatusEnum
	 */
	private Integer status;
	/**
	 * 摆摊id
	 */
	private Long id;
	/**
	 * 用户id
	 */
	private Long uid;
	/**
	 * 用户手机号
	 */
	private String mobile;
	private StallUpActivity activity;
}

