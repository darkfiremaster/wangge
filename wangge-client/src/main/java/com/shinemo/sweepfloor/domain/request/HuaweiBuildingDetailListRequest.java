package com.shinemo.sweepfloor.domain.request;

import lombok.Data;

@Data
public class HuaweiBuildingDetailListRequest {
    private String cellId;
    private String buildingId;
    private boolean treeFlag;
    private Integer treeLevel;
}
