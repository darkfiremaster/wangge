package com.shinemo.sweepvillage.domain.vo;

import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:28
 * @Desc
 */
@Data
public class SweepVillageActivityVO {
    private String title;
    private String villageId;
    private String villageName;
    /**
     * 村庄高德地图坐标
     */
    private String originLocation;
    /**
     * 村庄rgs坐标
     */
    private String location;
    private String area;
    private String areaCode;
    /** 地址信息 */
    private LocationDetailVO locationDetailVO;
}
