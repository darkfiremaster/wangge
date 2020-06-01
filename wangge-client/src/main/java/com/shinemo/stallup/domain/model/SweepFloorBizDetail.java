package com.shinemo.stallup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扫楼业务量统计
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SweepFloorBizDetail {

	private Long id;
	private String name;
	private Integer num;
}
