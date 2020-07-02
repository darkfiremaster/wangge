package com.shinemo.sweepvillage.domain.request;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/7/2 14:31
 * @Desc
 */
@Data
public class SweepVillageActivityQueryRequest extends QueryBase {

    private Long id;

    /**
     * 活动状态
     */
    private Integer status;

    /**
     * 查询类型 1:进行中 2:已结束
     */
    private Integer queryType;

    private String startTime;

    private String endTime;

}
