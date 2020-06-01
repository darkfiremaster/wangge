package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 楼栋vo
 */
@Data
public class BuildingDetailsVO {

    /** 楼栋id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 单元集合 */
    private List<UnitVO> unitVOS;

}
