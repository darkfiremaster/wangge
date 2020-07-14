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

    COMMON_APPLICATION("常用应用",0),
    BIZ_HANDLE("业务办理",1),
    QUICK_APPLICATION("快捷入口",2),
    DAOSANJIAO_SUPPORT("倒三角支撑",3),

    ;

    /**
     * 组名
     */
    private String name;

    /**
     * 分组id
     */
    private Integer group;
}