package com.shinemo.sweepfloor.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhaoyn
 * @Date 2019/6/12
 */
@AllArgsConstructor
@Getter
public enum HuaweiSweepFloorUrlEnum {
    //CHECK_NEW_USER("/video/p/i_queryNewUser.jsp", "判断是否新用户"),
    QUERY_CELL_LIST("/CMCC_GX_market/api/v2/queryCellList", "地图搜索","queryCellList"),
    ADD_BUILDING("/CMCC_GX_market/api/v2/addBuiling", "添加楼栋","addBuiling"),
    QUERY_BUILDING_LIST("/CMCC_GX_market/api/v2/queryBuildingList", "查询小区、楼栋信息","queryBuildingList"),
    EDIT_BUILDING("/CMCC_GX_market/api/v2/editBuilding", "编辑楼栋","editBuilding"),
    ADD_BUILDING_TENANTS("/CMCC_GX_market/api/v2/addBuildingTenants", "添加住户","addBuildingTenants"),
    EDIT_BUILDING_TENANTS("/CMCC_GX_market/api/v2/editBuildingTenants", "编辑住户","editBuildingTenants"),
    QUERY_BUILDING_DETAIL_LIST("/CMCC_GX_market/api/v2/queryBuildingDetailList", "楼栋信息查询（楼栋、单元、住户）","queryBuildingDetailList"),
    SYNCHRONIZE_SWEEPING_DATA("CMCC_GX_market/api/v2/synchronizeSweepingData", "同步扫楼数据至华为","synchronizeSweepingData"),
    ;
    private String url;
    private String desc;
    private String method;
}
