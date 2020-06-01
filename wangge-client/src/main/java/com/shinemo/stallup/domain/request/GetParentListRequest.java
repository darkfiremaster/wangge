package com.shinemo.stallup.domain.request;

import lombok.Data;

/**
 * @author Chenzhe Mao
 * @date 2020-05-25
 */
@Data
public class GetParentListRequest {
	private Long startTime;
	private Long endTime;
	private String seMobile;
	private Integer status;
	private String gridId;
	private Integer pageSize;
	private Integer currentPage;
}
