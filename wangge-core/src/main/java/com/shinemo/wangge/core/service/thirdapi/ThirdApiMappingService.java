package com.shinemo.wangge.core.service.thirdapi;

import com.shinemo.common.tools.result.ApiResult;

import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 14:03
 * @Desc
 */
public interface ThirdApiMappingService {

    ApiResult<Map<String,Object>> dispatch(Map<String, Object> requestData, String apiName);

    ApiResult<Map<String,Object>> asyncDispatch(Map<String, Object> requestData, String apiName);

}
