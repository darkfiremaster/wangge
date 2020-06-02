package com.shinemo.sweepfloor.domain.query;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SweepFloorVisitRecordingQuery {
    private Long id;
    /** 营销人员id */
    private String marketingUserId;
    /** 成功标识 */
    private Integer successFlag;
    /** 开始时间 */
    private Date startTime;
    /** 结束时间 */
    private Date endTime;
    /** 住户id */
    private String houseId;
    /** 住户id集合 */
    private List<String> houseIds;
    /** 小区id */
    private String communityId;
    /** 活动id集合 */
    private List<Long> activityIds;
}
