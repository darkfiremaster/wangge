package com.shinemo.smartgrid.domain;

import lombok.Data;

@Data
public class OutsideMarketingNumVO {
    /** 未开始摆摊数量 */
    private Integer stallUpNotStartCount;
    /** 已开始摆摊数量 */
    private Integer stallUpStartedCount;
    /** 已结束摆摊数量 */
    private Integer stallUpEndCount;
    /** 未开始扫楼数量 */
    private Integer sweepFloorNotStartCount;
    /** 已开始扫楼数量 */
    private Integer sweepFloorStartedCount;
    /** 已结束扫楼数量 */
    private Integer sweepFloorEndCount;
    /** 网格id */
    private String gridId;
    /** 网格名 */
    private String gridName;

}
