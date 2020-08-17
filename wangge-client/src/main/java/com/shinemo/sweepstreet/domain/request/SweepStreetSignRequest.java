package com.shinemo.sweepstreet.domain.request;

import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import lombok.Data;

import java.util.List;

@Data
public class SweepStreetSignRequest {
    /** 活动id */
    private Long activityId;
    /** 位置信息 */
    private LocationDetailVO locationDetailVO;
    /** 备注 */
    private String remark;
    /** 图片链接 */
    private List<String> picUrls;
}
