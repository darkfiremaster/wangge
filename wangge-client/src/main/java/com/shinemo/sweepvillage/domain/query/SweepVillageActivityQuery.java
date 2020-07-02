package com.shinemo.sweepvillage.domain.query;

import com.shinemo.client.common.QueryBase;
import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:24
 * @Desc
 */
@Data
public class SweepVillageActivityQuery extends QueryBase {
    private Long id;

    /**
     * 活动状态
     */
    private Integer status;

    private List<Integer> statusList;

    /**
     * 查询类型 1:进行中 2:已结束
     */
    private Integer queryType;

    private String startTime;

    private String endTime;

    private String mobile;

}
