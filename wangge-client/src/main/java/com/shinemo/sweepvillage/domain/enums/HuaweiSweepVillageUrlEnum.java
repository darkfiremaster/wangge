package com.shinemo.sweepvillage.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 类说明: 华为 扫村枚举类
 *
 * @author zengpeng
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum  HuaweiSweepVillageUrlEnum {
    ADD_SWEEPING_VILLAGE_DATA("CMCC_GX_market/api/v2/addVillageVisitRrecord", "新增走访记录接口","addSweepVillageData");

    private String url;
    private String desc;
    private String method;
}
