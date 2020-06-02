package com.shinemo.thirdapi.common.enums;

import com.shinemo.stallup.common.statemachine.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方接口状态枚举
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@AllArgsConstructor
public enum ThirdApiStatusEnum implements BaseEnum<ThirdApiStatusEnum> {

	NORMAL(1, "正常"),
	MOCK(0, "mock"),
	DELETE(-1, "删除");

	private @Getter
	final int id;
	private @Getter
	final String name;


}
