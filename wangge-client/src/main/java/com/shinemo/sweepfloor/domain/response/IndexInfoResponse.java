package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import com.shinemo.sweepfloor.domain.vo.SweepFloorActivityVO;
import lombok.Data;

/**
 * 首页扫楼信息
 */
@Data
public class IndexInfoResponse extends CommonHuaweiResponse {
    /** 进行中的扫楼 */
    private SweepFloorActivityVO sweepFloorActivityVO;
    /** 今日待办 */
    private Long todayToDo;
    /** 本周代办 */
    private Long weekToDo;
    /** 本月已办 */
    private Long monthDone;
}
