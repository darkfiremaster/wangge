package com.shinemo.sweepvillage.domain.query;

import com.shinemo.common.tools.query.Query;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SweepVillageVisitRecordingQuery extends Query {
    private Long id;
    /** 成功标识 */
    private Integer successFlag;
    /** 住户id */
    private String tenantsId;
    /** 住户id集合 */
    private List<String> tenantsIds;

    private Long activityId;

    private Integer status;


    private String mobile;
    /**
     * filterCreateTime为true时，根据创建时间过滤
     */
    private Boolean filterCreateTime;

    private Date startTime;
    private Date endTime;

}
