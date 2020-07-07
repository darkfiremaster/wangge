package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:37
 * @Desc
 */
@Data
public class VillageVO {

    private String id;

    private String name;

    private String gridId;

    private String area;

    private String areaCode;

    /**
     * 华为rgs坐标
     */
    private String location;

    /**
     * 高德地图坐标
     */
    private String originLocation;

    private String mobile;

    private Long createTime;
}
