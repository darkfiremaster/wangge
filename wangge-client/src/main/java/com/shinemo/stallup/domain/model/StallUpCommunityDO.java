package com.shinemo.stallup.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class StallUpCommunityDO {
    private Long id;
    /** 摆摊id */
    private Long activityId;
    /** 小区名 */
    private String communityName;
    /** 小区id */
    private String communityId;
    /** 小区详细地址 */
    private String communityAddress;
    /** 小区坐标 */
    private String communityLocation;
    /** 创建时间 */
    private Date gmtCreate;
    /** 更新时间 */
    private Date gmtModified;
}
