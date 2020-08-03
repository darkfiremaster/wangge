package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

/**
 * 添加住户华为返回值
 */
@Data
public class HuaWeiAddBuildingResponse extends CommonHuaweiResponse {

    private Build data;

    @Data
    public static class  Build{

        private String buildingId;
    }
}
