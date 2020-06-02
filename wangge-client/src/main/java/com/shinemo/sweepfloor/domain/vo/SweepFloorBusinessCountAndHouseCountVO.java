package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

@Data
public class SweepFloorBusinessCountAndHouseCountVO {
    /** 扫楼数量 */
    private Integer houseCount;
    /** 完成业务量 */
    private Integer businessCount;
}
