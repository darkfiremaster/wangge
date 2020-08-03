package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

/**
 * 华为返回的楼栋信息
 */
@Data
public class HuaweiBuildingResponse extends CommonHuaweiResponse {
    /** 楼栋id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 单元数 */
    private Integer unitCount;
    /** 每层户数 */
    private Integer householderCount;
    /** 宽带接入类型 */
    private String broadbandType;
    /** 宽带用户数 */
    private Integer broadbandUserCount;
    /** 楼层数 */
    private Integer floorNumber;
    /** 住户数 */
    private Integer liveCount;
    /** 端口剩余数 */
    private Integer remainingPortCount;
    /** 渗透率 */
    private String penetrationRate;
    /** 渗透率标签 */
    private List<String> penetrationLabel;

}
