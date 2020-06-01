package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

@Data
public class LocationDetailVO {
    /** 坐标 */
    private String location;
    /** 详细地址 */
    private String address;
}
