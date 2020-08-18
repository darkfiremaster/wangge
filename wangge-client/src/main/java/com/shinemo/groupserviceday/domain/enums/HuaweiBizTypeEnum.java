package com.shinemo.groupserviceday.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@AllArgsConstructor
public enum  HuaweiBizTypeEnum {
    PUBLIC("1","大众市场"),
    INFORMATION("2","信息化"),
    STREET_BIZ("3","扫街业务");

    @Getter
    private String id;
    @Getter
    private String name;
}
