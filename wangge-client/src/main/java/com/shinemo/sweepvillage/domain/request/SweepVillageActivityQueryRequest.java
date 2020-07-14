package com.shinemo.sweepvillage.domain.request;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/7/2 14:31
 * @Desc
 */
@Data
public class SweepVillageActivityQueryRequest extends QueryBase {

    private Long id;

    /**
     * 活动状态  1:进行中 2:已结束
     */
    private Integer status;


    private Date startTime;

    private Date endTime;

    private Long pageSize;
    private Long currentPage;



}
