package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SweepFloorActivityVO {
    private Long id;
    /** 小区名 */
    private String communityName;
    /** 小区id */
    private String communityId;
    /** 详细地址 */
    private String address;
    /** 小区坐标地址 */
    private String location;
    /** 楼栋数 */
    private Integer buildingCount;
    /** 住户数 */
    private Integer householderCount;
    /** 端口余量 */
    private Integer remainingPortCount;
    /** 渗透率 */
    private String penetrationRate;
    /** 状态 */
    private Integer status;
    /** 实际开始时间 */
    private Date startTime;
    /** 结束时间 */
    private Date endTime;
    /** 完成业务量 */
    private Integer businessCount;
    /** 走访数量 */
    private Integer houseCount;
    /** 网格id */
    private String gridId;
    /** 网格名 */
    private String gridName;
    /** 创建时间 */
    private Date gmtCreate;
    /** 创建人姓名 */
    private String creatorName;
    /** 异常原因 */
    private String abnormalReason;

}
