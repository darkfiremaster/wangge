package com.shinemo.sweepfloor.domain.query;


import lombok.Data;

@Data
public class SweepFloorBuildingQuery {
    /** 小区id */
    private String communityId;
    /** 楼栋id */
    private String buildingId;
    /** 渗透率 */
    private String penetrationRate;
    /** 类型：查询地址选择、查询平面图 */
    private Integer type;
}
