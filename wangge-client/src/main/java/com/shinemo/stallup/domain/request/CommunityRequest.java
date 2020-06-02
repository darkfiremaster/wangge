package com.shinemo.stallup.domain.request;

import lombok.Builder;
import lombok.Data;

/**
 * 小区信息查询
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Data
@Builder
public class CommunityRequest {
	/**
	 * 小区id
	 */
	private String id;
}
