package com.shinemo.wangge.core.service.thirdapi;

import com.shinemo.common.tools.result.ApiResult;

import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 14:03
 * @Desc 华为旧接口的调用方式, 接口4.1-4.38(包含)的接口都用这个类
 */
public interface ThirdApiMappingService {

    /**
     * 调用华为老接口
     */
    ApiResult<Map<String,Object>> dispatch(Map<String, Object> requestData, String apiName);

    ApiResult<Map<String,Object>> asyncDispatch(Map<String, Object> requestData, String apiName,String mobile);

}
