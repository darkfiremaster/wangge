package com.shinemo.sweepfloor.domain.query;

import com.shinemo.common.tools.query.Query;
import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SignRecordQuery extends Query {
    private Long id;
    /** 活动id */
    private Long activityId;
    /** 活动Id集合 */
    private List<Long> activityIds;
    /** 照片地址 */
    private List<String> imgUrl;
    /** 业务类型 */
    private Integer bizType;
    /** 用户id */
    private String userId;
    /** 地址信息 */
    private LocationDetailVO locationDetailVO;

    private String remark;
    private Date startTime;
    private Date endTime;
}
