package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

@Data
public class HuaweiCellsAndBuildingsResponseWrapper extends CommonHuaweiResponse {

    private CellsAndBuildings data;

    @Data
    public static class CellsAndBuildings {

        List<HuaweiCellsAndBuildingsResponse> cellBuildingList;
    }
}
