package com.shinemo.stallup.domain.request;

import lombok.Data;

import java.util.List;

/**
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
public class GetCustDetailRequest {
	private String communityId;
	private List<String> idList;
	private Integer page;
	private Integer pageSize;
}
