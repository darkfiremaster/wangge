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

	GROUP_SERVICE_DAY_TOOL(9, "集团服务日工具"),
	PUBLIC_GROUP_SERVICE_DAY_BIZ(10, "集团服务日公共业务"),
	PUBLIC_GROUP_SERVICE_DAY_BIZ_DATA(11, "集团服务日公共业务数据头"),
	INFORMATION_GROUP_SERVICE_DAY_BIZ(12,"集团服务日政企专属"),
	INFORMATION_GROUP_SERVICE_DAY_BIZ_DATA(13,"集团服务日政企专属数据头"),

	SWEEP_STREET_TOOL(14, "扫街工具"),
	SWEEP_STREET_BIZ(15, "扫街业务"),
	SWEEP_STREET_BIZ_DATA(16, "扫街业务数据头");

	private Integer type;
	private String desc;
}
