package com.shinemo.sweepfloor.domain.vo;

import com.shinemo.stallup.domain.model.SweepFloorBizDetail;
import lombok.Data;

import java.util.List;

/**
 * 办理量VO
 */
@Data
public class SweepFloorMarketingNumberVO {
    /** 扫楼活动id */
    private Long activityId;

    List<SweepFloorBizDetail> bizDetails;
    /** 备注 */
    private String remark;

    /** 营销人员名 */
    private String userName;
}
