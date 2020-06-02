package com.shinemo.sweepfloor.domain.vo;

import com.shinemo.stallup.domain.model.StallUpBizType;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 扫楼走访记录vo
 */
@Data
public class SweepFloorVisitRecordingVO {
    /** 扫楼活动id */
    private Long activityId;
    /** 小区id */
    private String communityId;
    /** 楼栋id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 单元id */
    private String unitId;
    /** 单元名 */
    private String unitName;
    /**门牌号 */
    private String houseNumber;
    /** 住户id */
    private String houseId;
    /** 业务类型 */
    private List<StallUpBizType> businessType;
    /** 是否投诉敏感客户标识 */
    private Integer complaintSensitiveCustomersFlag;
    /** 是否成功标识 */
    private Integer successFlag;
    /**宽带过期时间 */
    private Date broadbandExpireDate;
    /** 联系人 */
    private String contactName;
    /** 联系人手机号 */
    private String contactMobile;
    /** 备注 */
    private String remark;
    /** 走访时间 */
    private Date visitTime;
    /** 营销人员名 */
    private String marketingUserName;
    /** 类型 ,0历史数据，1非历史数据*/
    private Integer type;
}
