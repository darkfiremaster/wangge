package com.shinemo.groupserviceday.domain.request;

import lombok.Data;

@Data
public class GroupServiceDaySignRequest {
    /** 活动id */
    private Long id;
    /** 坐标 */
    private String location;
    /** 打卡位置 */
    private String address;
}
