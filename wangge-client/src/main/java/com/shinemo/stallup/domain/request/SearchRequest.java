package com.shinemo.stallup.domain.request;

import lombok.Builder;
import lombok.Data;

/**
 * 华为地图搜索请求
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
public class SearchRequest {
	/**
	 * 关键词
	 */
	private String keywords;
}
