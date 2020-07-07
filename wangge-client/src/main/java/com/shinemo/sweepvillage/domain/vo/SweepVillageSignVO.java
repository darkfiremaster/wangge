package com.shinemo.sweepvillage.domain.vo;

import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/7/3 17:42
 * @Desc
 */
@Data
public class SweepVillageSignVO {

    private Long sweepVillageActivityId;

    private String remark;

    private String imgUrl;

    /** 地址信息 */
    private LocationDetailVO locationDetailVO;

}
