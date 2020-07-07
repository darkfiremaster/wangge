package com.shinemo.wangge.web.controller.sweepvillage;

import com.google.gson.*;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.sweepvillage.domain.vo.SweepVillageTenantsVO;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(GsonUtils.toJson(data.get("rows"))).getAsJsonArray();
        List<SweepVillageTenantsVO> list = GsonUtils.jsonArrayToList(jsonArray, SweepVillageTenantsVO.class);
        if (!CollectionUtils.isEmpty(list)) {

            for (SweepVillageTenantsVO tenantsVO: list) {
                String contactMobile = tenantsVO.getContactMobile();
                String contactPerson = tenantsVO.getContactPerson();
                //脱敏手机号、姓名
                String mobile = SmartGridUtils.desensitizationMobile(contactMobile);
                String name = SmartGridUtils.desensitizationName(contactPerson);
                tenantsVO.setContactPerson(name);
                tenantsVO.setContactMobile(mobile);
            }
        }
        data.put("rows",list);
        return apiResult;
    }

    @PostMapping("/queryVillageTenantListSearch")
    public ApiResult<Map<String,Object>> queryVillageTenantListSearch(@RequestBody Map<String,Object> requestData) {
        ApiResult<Map<String, Object>> apiResult = thirdApiMappingService.dispatch(requestData, "queryVillageTenantListSearch");
        if (apiResult == null || !apiResult.isSuccess()) {
            return apiResult;
        }
        Map<String, Object> data = apiResult.getData();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(GsonUtils.toJson(data.get("rows"))).getAsJsonArray();
        List<SweepVillageTenantsVO> list = GsonUtils.jsonArrayToList(jsonArray, SweepVillageTenantsVO.class);
        if (!CollectionUtils.isEmpty(list)) {

            for (SweepVillageTenantsVO tenantsVO: list) {
                String contactMobile = tenantsVO.getMobile();
                String contactPerson = tenantsVO.getContactPerson();
                //脱敏手机号、姓名
                String mobile = SmartGridUtils.desensitizationMobile(contactMobile);
                String name = SmartGridUtils.desensitizationName(contactPerson);
                tenantsVO.setContactPerson(name);
                tenantsVO.setMobile(mobile);
            }
        }
        data.put("rows",list);
        return apiResult;
    }

    @PostMapping("/addTenants")
    public ApiResult<Map<String,Object>> addTenants(@RequestBody Map<String,Object> requestData) {
        requestData.put("activityId","" + requestData.get("activityId"));
        return thirdApiMappingService.dispatch(requestData,"addTenants");
    }

}
