package com.shinemo.stallup.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Chenzhe Mao
 * @date 2020-05-09
 */
@AllArgsConstructor
@Getter
public enum BusinessConfigEnum {
	STALL_UP_BIZ(2, "摆摊业务办理"),
	STALL_UP_MARKET(3, "摆摊营销工具"),
	INDEX_BIZ(4, "首页业务"),
	SWEEP_FLOOR(5, "扫楼工具"),
	SWEEP_FLOOR_BIZ(6, "扫楼业务"),
	SWEEP_VILLAGE(7,"扫村工具"),
	SWEEP_VILLAGE_BIZ(8,"扫村业务"),
	DANSANJIAO_SUPPORT_BIZ(9,"倒三角支撑"),


	;

	private Integer type;
	private String desc;
}
