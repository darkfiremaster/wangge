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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SweepFloorBizTotal {
	/** 扫楼数量 */
	private Integer houseCount;
	/** 完成业务量 */
	private Integer businessCount;
}
