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
    ADD_SWEEPING_VILLAGE_DATA("CMCC_GX_market/api/v2/addVillageVisitRrecord", "扫村新增走访记录接口","addVillageVisitRrecord","addVisitRecording"),
    UPDATE_SWEEPING_VILLAGE_DATA("CMCC_GX_market/api/v2/updateVillageVisitRrecord", "扫村编辑走访记录接口","updateVillageVisitRrecord","updateVisitRecording"),
    DELETE_SWEEPING_VILLAGE_DATA("CMCC_GX_market/api/v2/deleteVillageVisitRrecord", "扫村删除走访记录接口","deleteVillageVisitRrecord","deleteVisitRecording"),
    ADD_OR_UPDATE_VILLAGE_BIZ_DATA("CMCC_GX_market/api/v2/villagePlanBusiness", "新增/编辑扫村计划业务办理量接口","villagePlanBusiness","addBusiness");
    private String url;
    private String desc;
    private String method;
    private String apiName;
}
