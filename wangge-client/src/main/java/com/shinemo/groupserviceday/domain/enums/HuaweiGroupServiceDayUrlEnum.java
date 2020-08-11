package com.shinemo.groupserviceday.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum HuaweiGroupServiceDayUrlEnum {
    GET_GROUP_LIST("/CMCC_GX_market/api/v2/getGroupList","获取集团列表","","getGroupList"),
    CREATE_GROUP_SERVICE_DAY("/CMCC_GX_market/api/v2/","新建集团服务日计划","","createGroupServiceDay"),
    UPDATE_GROUP_SERVICE_DAY("/CMCC_GX_market/api/v2/","更新集团服务日计划","","updateGroupServiceDay"),


    ADD_OR_UPDATE_GROUP_SERVICE_DAY_DATA("/CMCC_GX_market/api/v2/", "集团服务日新增走访记录接口","","addBusiness")

    ;

    private String url;
    private String desc;
    private String method;
    private String apiName;
}
