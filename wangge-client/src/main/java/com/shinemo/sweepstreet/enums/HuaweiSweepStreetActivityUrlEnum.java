package com.shinemo.sweepstreet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum HuaweiSweepStreetActivityUrlEnum {
    CREATE_SWEEP_STREET_ACTIVITY("/SGCoreMarketing/XX/XX","新建扫街活动","",""),
    ;

    private String url;
    private String desc;
    private String method;
    private String apiName;
}
