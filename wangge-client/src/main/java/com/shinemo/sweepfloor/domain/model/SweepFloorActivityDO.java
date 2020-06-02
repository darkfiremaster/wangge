package com.shinemo.sweepfloor.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class SweepFloorActivityDO {
    private Long id;
    /** 小区名 */
    private String communityName;
    /** 小区id */
    private String communityId;
    /** 详细地址 */
    private String address;
    /** 小区坐标地址 */
    private String location;
    /** 创建者 */
    private String creator;
    /** 创建人姓名 */
    private String creatorName;
    /** 创建人单位id */
    private String creatorOrgId;
    /** 状态 */
    private Integer status;
    private Date gmtCreate;
    private Date gmtModified;
    private String mobile;
    private String gridId;
    private Date startTime;
    private Date endTime;

}
