package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

/**
 * 华为返回的单元信息
 */
@Data
public class HuaweiUnitResponse extends CommonHuaweiResponse {
    /** 单元名 */
    private String unitName;
    /** 单元id */
    private String unitId;
    /** 住户集合 */
    private List<HuaweiHouseResponse> houseList;
}
