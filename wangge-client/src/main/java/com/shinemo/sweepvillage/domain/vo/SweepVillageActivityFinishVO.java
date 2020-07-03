package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:40
 * @Desc
 */
@Data
public class SweepVillageActivityFinishVO {

    private Long id;

    /**
     * 扫村次数
     */
    private Integer sweepVillageCount;

    /**
     * 走访户数
     */
    private Integer visitUserCount;


}
