package com.shinemo.sweepfloor.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class HuaweiCellAndBuildingsRequest {
    private List<String> cellIds;
    private  String penetrationRate;
    private String buildingId;
}
