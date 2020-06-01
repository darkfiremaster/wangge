package com.shinemo.stallup.domain.model;

import lombok.Data;

/**
 * 摆摊详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
public class StallUpDetailVO {
	private Long id;
	private String title;
	private Integer bizTotal;
	private String realStartTime;
	private String realEndTime;
	private Boolean isAutoEnd;
	private String address;
	private String startTime;
	private String endTime;
	private String location;
	private String bizTypeStr;
	private String communityId;
}