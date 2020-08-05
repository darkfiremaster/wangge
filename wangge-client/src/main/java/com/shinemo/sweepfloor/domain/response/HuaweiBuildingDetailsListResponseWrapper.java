package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

@Data
public class HuaweiBuildingDetailsListResponseWrapper extends CommonHuaweiResponse {

    private HuaweiBuildingDetailsList data;

    @Data
    public static class HuaweiBuildingDetailsList {

        List<HuaweiBuildingDetailsListResponse> buildingList;
    }
}
