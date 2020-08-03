package com.shinemo.sweepfloor.domain.request;

import lombok.Data;

@Data
public class HuaweiBuildingRequest {
    /** 楼栋Id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 单元数 */
    private Integer unitCount;
    /** 楼层数 */
    private Integer floorNumber;
    /** 每层户数 */
    private Integer householderCount;
    /** 宽带接入类型 */
    private String broadbandType;
    /** 宽带用户数 */
    private Integer broadbandUserCount;
    /** 端口剩余 */
    private Integer remainingPortCount;
    /** 小区Id */
    private String cellId;
}
