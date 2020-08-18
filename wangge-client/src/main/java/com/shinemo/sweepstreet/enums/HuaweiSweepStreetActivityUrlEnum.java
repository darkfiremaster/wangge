package com.shinemo.sweepstreet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum HuaweiSweepStreetActivityUrlEnum {
    CREATE_SWEEP_STREET_ACTIVITY("/SGCoreMarketing/groupService/addStreetScanPlan","新建扫街活动","","addStreetScanPlan"),
    ADD_OR_UPDATE_SWEEP_STREET_ACTIVITY_DATA("/SGCoreMarketing/groupService/saveStreetScanBiz","新增/编辑业务办理量","","saveStreetScanBiz"),
    CREATE_SWEEP_STREET_VISITRECORD("addVisitRecord","新增走访记录","","addVisitRecord"),
    FIND_MERCHANT_LIST("/SGCoreMarketing/groupService/getNearbyGroupList","查询商户列表","","getNearbyGroupList");

    private String url;
    private String desc;
    private String method;
    private String apiName;
}
