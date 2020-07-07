package com.shinemo.wangge.web.controller.sweepvillage;

import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.sweepvillage.domain.vo.SweepVillageTenantsVO;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sweepvillage")
public class TenantsController {

    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    @PostMapping("/getTenantsList")
    public ApiResult<Map<String,Object>> getTenantsList(@RequestBody Map<String,Object> requestData) {
        ApiResult<Map<String, Object>> apiResult = thirdApiMappingService.dispatch(requestData, "getTenantsList");
        if (apiResult == null || !apiResult.isSuccess()) {
            return apiResult;
        }
        Map<String, Object> data = apiResult.getData();
        List<Map<String,Object>> list = (List<Map<String,Object>>)data.get("rows");

        if (!CollectionUtils.isEmpty(list)) {

            for (Map map: list) {
                String contactPerson = (String)map.get("contactPerson");
                String contactMobile = (String)map.get("contactMobile");
                //脱敏手机号、姓名
                String mobile = SmartGridUtils.desensitizationMobile(contactMobile);
                String name = SmartGridUtils.desensitizationName(contactPerson);
                map.put("contactPerson",name);
                map.put("contactMobile",mobile);
            }
        }
        return apiResult;
    }

    @PostMapping("/queryVillageTenantListSearch")
    public ApiResult<Map<String,Object>> queryVillageTenantListSearch(@RequestBody Map<String,Object> requestData) {
        ApiResult<Map<String, Object>> apiResult = thirdApiMappingService.dispatch(requestData, "queryVillageTenantListSearch");
        if (apiResult == null || !apiResult.isSuccess()) {
            return apiResult;
        }
        Map<String, Object> data = apiResult.getData();
        List<Map<String,Object>> list = (List<Map<String,Object>>)data.get("rows");

        if (!CollectionUtils.isEmpty(list)) {

            for (Map map: list) {
                String contactPerson = (String)map.get("contactPerson");
                String contactMobile = (String)map.get("mobile");
                //脱敏手机号、姓名
                String mobile = SmartGridUtils.desensitizationMobile(contactMobile);
                String name = SmartGridUtils.desensitizationName(contactPerson);
                map.put("contactPerson",name);
                map.put("mobile",mobile);
            }
        }
        return apiResult;
    }

}
