package com.shinemo.groupserviceday.domain.request;

import lombok.Data;

@Data
public class GroupServiceDayPartnerListRequest {
    /** 城市code */
    private String cityCode;
    /** 地区code */
    private String areaCode;
    /** 网格id */
    private String gridId;
    /** 模糊查询参数 */
    private String queryParam;
}
