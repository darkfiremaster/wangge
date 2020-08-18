package com.shinemo.sweepstreet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum HuaweiSweepStreetActivityUrlEnum {
    CREATE_SWEEP_STREET_ACTIVITY("/SGCoreMarketing/XX/XX","新建扫街活动","",""),
    ADD_OR_UPDATE_SWEEP_STREET_ACTIVITY_DATA("ADD_OR_UPDATE_GROUP_SERVICE_DAY_DATA","新增/编辑业务办理量","","saveStreetScanBiz")
    ;

    private String url;
    private String desc;
    private String method;
    private String apiName;
}
