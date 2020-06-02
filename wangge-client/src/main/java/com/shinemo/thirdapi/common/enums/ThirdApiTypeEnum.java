package com.shinemo.thirdapi.common.enums;

import com.shinemo.stallup.common.statemachine.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方接口类型枚举
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@AllArgsConstructor
public enum ThirdApiTypeEnum implements BaseEnum<ThirdApiTypeEnum> {

	HUAWEI(1, "华为");

	private @Getter
	final int id;
	private @Getter
	final String name;


}
