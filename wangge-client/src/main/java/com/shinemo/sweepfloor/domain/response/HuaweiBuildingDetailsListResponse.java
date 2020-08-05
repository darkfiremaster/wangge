package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

/**
 * 楼栋信息（楼栋、单元、住户）
 */
@Data
public class HuaweiBuildingDetailsListResponse {
    /** 楼栋id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 单元集合 */
    private List<HuaweiUnitResponse> unitList;
}
