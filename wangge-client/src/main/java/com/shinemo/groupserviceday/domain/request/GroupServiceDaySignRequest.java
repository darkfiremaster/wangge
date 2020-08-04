package com.shinemo.groupserviceday.domain.request;

import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import lombok.Data;

import java.util.List;

@Data
public class GroupServiceDaySignRequest {
    /** 活动id */
    private Long id;
    /** 位置信息 */
    private LocationDetailVO locationDetailVO;
    /** 备注 */
    private String remark;
    /** 图片链接 */
    private List<String> picUrls;
}
