package com.shinemo.stallup.domain.params;

import lombok.Data;

/**
 * @author Chenzhe Mao
 * @date 2020-05-08
 */
@Data
public class DuDaoParams extends BizParams{
	/**
	 * 业务参数
	 */
	private Integer resId;
	/**
	 * 方法名
	 */
	private String methodName;
}
