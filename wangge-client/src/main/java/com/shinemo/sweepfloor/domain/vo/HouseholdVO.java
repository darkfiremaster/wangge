package com.shinemo.sweepfloor.domain.vo;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 住户vo
 */
@Data
public class HouseholdVO {
    /** 住户id */
    private String houseId;
    /** 是否有宽带 */
    private Integer broadbandFlag;
    /** 宽带到期时间 */
    private Date broadbandExpireTime;
    /** 最新走访时间 */
    private Date visitTime;
    /** 运营商类型 */
    private List<String> serviceProvider;
    /** 门牌号 */
    private String houseNumber;
    /** 楼栋名 */
    private String buildingName;
    /** 楼栋id */
    private String buildingId;
    /** 小区id */
    private String communityId;
    /** 单元名 */
    private String unitName;
    /** 单元id */
    private String unitId;
    /** 家庭成员 */
    private List<FamilyMember> familyMembers;
    /** 标签 */
    private List<String> labels;
    /** 宽带备注 */
    private String broadbandRemark;
    /** 宽带月租 */
    private Double broadbandMonthlyrent;
    /** 电视盒类型 */
    private List<String> TVBoxTypes;
    /** 电视盒备注 */
    private String TVBoxRemark;
    /** 电视盒到期时间 */
    private Date TVBoxExpireTime;
}
