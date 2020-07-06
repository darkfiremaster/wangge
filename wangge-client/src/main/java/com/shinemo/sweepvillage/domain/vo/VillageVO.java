package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:37
 * @Desc
 */
@Data
public class VillageVO {

    private String villageId;

    private String name;

    private String gridId;

    private String area;

    private String areaCode;

    private String location;

    private String rgsLocation;

    private String mobile;

    private Long createTime;
}
