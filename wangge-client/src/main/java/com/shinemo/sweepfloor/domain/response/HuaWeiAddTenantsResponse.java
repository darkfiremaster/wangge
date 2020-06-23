package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

import java.util.List;

/**
 * 添加住户华为返回值
 */
@Data
public class HuaWeiAddTenantsResponse extends CommonHuaweiResponse {

    private Tenant data;

    @Data
    public static class  Tenant{

        private String houseId;
    }
}
