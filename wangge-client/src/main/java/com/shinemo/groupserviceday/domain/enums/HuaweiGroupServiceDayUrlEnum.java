package com.shinemo.groupserviceday.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum HuaweiGroupServiceDayUrlEnum {
    ADD_OR_UPDATE_GROUP_SERVICE_DAY_DATA("CMCC_GX_market/api/v2/", "集团服务日新增走访记录接口","","");

    private String url;
    private String desc;
    private String method;
    private String apiName;
}
