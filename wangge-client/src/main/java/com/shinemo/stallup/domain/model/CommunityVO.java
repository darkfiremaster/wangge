package com.shinemo.stallup.domain.model;

import lombok.Data;

/**
 * 小区对象
 */
@Data
public class CommunityVO {
    /** 小区名 */
    private String communityName;
    /** 小区id */
    private String communityId;
    /** 小区详细地址 */
    private String communityAddress;
    /** 小区坐标 */
    private String communityLocation;
}
