package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

@Data
public class LocationDetailVO {
    /** 打卡人坐标 */
    private String location;
    /** 打卡人详细地址 */
    private String address;
}
