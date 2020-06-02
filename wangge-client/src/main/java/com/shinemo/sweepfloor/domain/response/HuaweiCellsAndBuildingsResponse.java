package com.shinemo.sweepfloor.domain.response;


import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

/**
 * 查询华为小区、楼栋信息返回值
 */
@Data
public class HuaweiCellsAndBuildingsResponse extends CommonHuaweiResponse {
    /** 小区名 */
    private String cellName;
    /** 小区id */
    private String cellId;
    /** 渗透率 */
    private String penetrationRate;
    /** 渗透率标签 */
    private List<String> penetrationLabel;
    /** 楼栋集合 */
    private List<HuaweiBuildingResponse> buildingList;
}
