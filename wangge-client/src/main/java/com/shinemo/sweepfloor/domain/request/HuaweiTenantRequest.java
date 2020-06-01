package com.shinemo.sweepfloor.domain.request;


import com.shinemo.sweepfloor.domain.vo.FamilyMember;
import lombok.Data;

import java.util.List;

@Data
public class HuaweiTenantRequest {
    /** 小区Id */
    private String cellId;
    /** 住户id */
    private String houseId;
    /** 楼栋id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 单元id */
    private String unitId;
    /** 门牌号 */
    private String houseNumber;
    /** 运营商 */
    private String serviceProvider;
    /** 宽带到期时间 */
    private String broadbandExpireTime;
    /**
     * 是否有家庭宽带
     * 1、有
     * 2、没有
     */
    private Integer broadbandFlag;
    /** 家庭成员 */
    private List<FamilyMember> familyMembers;
}
