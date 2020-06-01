package com.shinemo.stallup.domain.params;

import lombok.Data;

/**
 * @author Chenzhe Mao
 * @date 2020-05-08
 */
@Data
public class SuiShenXingParams extends BizParams{
	/**
	 * 下载链接
	 */
	private String downloadUrl;
	/**
	 * 类型 1-新入网 2-随身行
	 */
	private Integer type;
}
