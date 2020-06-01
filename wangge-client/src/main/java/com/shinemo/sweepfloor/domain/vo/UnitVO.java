package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 单元vo
 */
@Data
public class UnitVO {
    /** 单元id */
    private String unitId;
    /** 单元名 */
    private String unitName;
    /** 住户信息集合 */
    private List<HouseholdVO> householdVOS;
}
