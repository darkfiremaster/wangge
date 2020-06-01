package com.shinemo.stallup.domain.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 摆摊详情
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Data
public class StallUpDetail {
	private Long id;
	private String title;
	private Integer bizTotal;
	private Date realStartTime;
	private Date realEndTime;
	private Integer status;
	private String address;
	private Date startTime;
	private Date endTime;
	private String location;
	private String bizList;
	private Long parentId;
	private String communityId;
	private String mobile;
	private String name;
	private String detail;
}
