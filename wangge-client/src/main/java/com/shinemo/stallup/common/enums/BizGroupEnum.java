package com.shinemo.stallup.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全部应用页面的分组枚举
 *
 * @author Chenzhe Mao
 * @date 2020-04-30
 */
@AllArgsConstructor
@Getter
public enum BizGroupEnum {

    COMMON_APPLICATION("常用应用"),
    ALL_APPLICATION("全部应用"),
    QUICK_APPLICATION("快捷入口"),
    DAOSANJIAO_SUPPORT("倒三角支撑"),

    ;

    /**
     * 组名
     */
    private String name;

}