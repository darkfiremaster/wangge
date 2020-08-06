package com.shinemo.wangge.core.service.thirdapi.impl;

import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.http.HttpConnectionUtils;
import com.shinemo.smartgrid.http.HttpResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.smartgrid.utils.ThreadPoolUtil;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.sweepfloor.domain.model.HuaweiApiLogDO;
import com.shinemo.thirdapi.common.enums.ThirdApiStatusEnum;
import com.shinemo.thirdapi.common.enums.ThirdApiTypeEnum;
import com.shinemo.thirdapi.common.error.ThirdApiErrorCodes;
import com.shinemo.thirdapi.domain.model.ThirdApiMappingDO;
import com.shinemo.wangge.core.config.exception.HuaweiApiTimeoutException;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiCacheManager;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.dal.mapper.HuaweiApiLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 14:03
 * @Desc
 */
@Slf4j
@Service
public class ThirdApiMappingV2ServiceImpl implements ThirdApiMappingV2Service {


    @Value("${smartgrid.huawei.accessKeyId}")
    public String accessKeyId;
    @Value("${smartgrid.huawei.secretKey}")
    public String secretKey;
    @Value("${smartgrid.huawei.auth.domain}")
    public String authDomain;


    @Value("${smartgrid.huawei.signkey}")
    public String signkey;
    @Value("${smartgrid.huawei.aesKey}")
    public String aeskey;

    @Resource
    private HuaweiApiLogMapper huaweiApiLogMapper;

    private static final String HUAWEI_SUCCESS_CODE = "0";
    private static final String HUAWEI_RESPONSE_PARAM_CODE = "resultCode";
    private static final String HUAWEI_RESPONSE_PARAM_DATA = "data";
    private static final String HUAWEI_RESPONSE_PARAM_MESSAGE = "resultDesc";
    private static final String MOBILE_PARAM = "mobile";


    @Override
    public ApiResult<Map<String, Object>> dispatch(Map<String, Object> requestData, String apiName) {
        //从缓存中获取数据
        ThirdApiMappingDO thirdApiMappingDO = ThirdApiCacheManager.THIRD_API_CACHE.get(apiName);

        if (thirdApiMappingDO == null) {
            log.error("[dispatch] url不存在,apiName:{},request:{}", apiName, requestData);
            throw new ApiException("url不存在");
        }

        //根据业务类型判断调谁的接口
        if (isHuaweiApi(thirdApiMappingDO)) {
            if (isMock(thirdApiMappingDO)) {
                return handleMockRequest(requestData, thirdApiMappingDO);
            }

            //调华为接口
            String mobile = getMobile();
            if (StringUtils.isBlank(mobile)) {
                //当异步调用时,会无法从SmartGridContext获取到手机号,所以从请求参数中获取
                mobile = (String) requestData.get(MOBILE_PARAM);
            }

            if (!thirdApiMappingDO.isIgnoreMobile()) {
                requestData.put(MOBILE_PARAM, mobile);
            }

            Map<String, Object> header = SmartGridUtils.buildHeader(mobile, accessKeyId, secretKey);
            String param = GsonUtils.toJson(requestData);
            //String param = getRequestParam(requestData, thirdApiMappingDO.getMethod());
            HttpResult httpResult = HttpConnectionUtils.httpPost(authDomain + thirdApiMappingDO.getUrl(), param, header);

            insertApiLog(thirdApiMappingDO.getUrl(), httpResult, param, mobile);

            return handleResult(requestData, thirdApiMappingDO, param, httpResult);
        }

        return ApiResultWrapper.fail(ThirdApiErrorCodes.API_TYPE_ERROR);
    }

    @Retryable(value = {HuaweiApiTimeoutException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    @Async
    @Override
    public ApiResult<Map<String, Object>> asyncDispatch(Map<String, Object> requestData, String apiName, String mobile) {
        requestData.put("mobile", mobile);
        return dispatch(requestData, apiName);
    }


    @Recover
    public ApiResult<Map<String, Object>> asyncDispatchRecover(Exception e, Map<String, Object> map, String apiName, String mobile) {
        log.error("[asyncDispatchRecover] request:{},apiName:{},mobile:{},exception:{}", map, apiName, mobile, e.getMessage());
        //todo 补偿处理
        return ApiResultWrapper.fail(ThirdApiErrorCodes.BASE_ERROR);
    }

    private String getRequestParam(Map<String, Object> requestData, String method) {
        return SmartGridUtils.buildRequestParam(method, requestData, signkey);
    }

    private String getMobile() {
        return SmartGridContext.getMobile();
    }

    private boolean isMock(ThirdApiMappingDO thirdApiMappingDO) {
        return thirdApiMappingDO.getStatus().equals(ThirdApiStatusEnum.MOCK.getId());
    }

    private boolean isHuaweiApi(ThirdApiMappingDO thirdApiMappingDO) {
        return Objects.equals(thirdApiMappingDO.getType(), ThirdApiTypeEnum.HUAWEI.getId());
    }

    private ApiResult<Map<String, Object>> handleResult(Map<String, Object> requestData, ThirdApiMappingDO thirdApiMappingDO, String param, HttpResult httpResult) {
        if (httpResult != null && httpResult.timeOut()) {
            //超时处理
            log.error("[dispatch] 华为接口超时 ,url={}, request={}, param={}, httpResult = {}",
                    thirdApiMappingDO.getUrl(), requestData, param, httpResult);
            throw new HuaweiApiTimeoutException(500, "接口超时,请稍后再试");
        }


        if (httpResult != null && httpResult.success()) {
            Map<String, Object> huaweiResponse = getJsonMap(httpResult.getContent());
            if (huaweiResponse == null) {
                log.error("[dispatch] huawei api error,url={}, request={}, param={}, httpResult = {}",
                        thirdApiMappingDO.getUrl(), requestData, param, httpResult);
                return ApiResultWrapper.fail(ThirdApiErrorCodes.HUA_WEI_RESPONSE_IS_NULL);
            }

            if (!huaweiRequestSuccess(huaweiResponse)) {
                log.error("[dispatch] huawei api error,url={}, request={}, param={}, huaweiResponse = {}",
                        thirdApiMappingDO.getUrl(), requestData, param, huaweiResponse);
                return ApiResult.fail(huaweiResponse.get(HUAWEI_RESPONSE_PARAM_MESSAGE).toString(), ThirdApiErrorCodes.HUA_WEI_ERROR.code);
            }

            Map<String, Object> result = getJsonMap(GsonUtils.toJson(huaweiResponse.get(HUAWEI_RESPONSE_PARAM_DATA)));
            dealPage(thirdApiMappingDO, result);
            return ApiResult.of(0, result);
        }
        log.error("[dispatch] http error,url = {}, request = {}, param = {}", thirdApiMappingDO.getUrl(), requestData, param);
        return ApiResultWrapper.fail(ThirdApiErrorCodes.BASE_ERROR);
    }

    private ApiResult<Map<String, Object>> handleMockRequest(Map<String, Object> requestData, ThirdApiMappingDO thirdApiMappingDO) {
        log.info("[dispatch] 返回mock数据,url:{}, request:{}, result:{}", thirdApiMappingDO.getUrl(), requestData, thirdApiMappingDO.getMockData());
        Map<String, Object> result = getJsonMap(thirdApiMappingDO.getMockData());
        if (huaweiRequestSuccess(result)) {
            Map<String, Object> objectMap = getJsonMap(GsonUtils.toJson(result.get(HUAWEI_RESPONSE_PARAM_DATA)));
            dealPage(thirdApiMappingDO, objectMap);
            result.put(HUAWEI_RESPONSE_PARAM_DATA, objectMap);
            return ApiResult.of(0, getJsonMap(GsonUtils.toJson(result.get(HUAWEI_RESPONSE_PARAM_DATA))));
        } else {
            return ApiResult.fail(result.get(HUAWEI_RESPONSE_PARAM_MESSAGE).toString(), ThirdApiErrorCodes.HUA_WEI_ERROR.code);
        }
    }


    private void dealPage(ThirdApiMappingDO thirdApiMappingDO, Map<String, Object> objectMap) {
        if (thirdApiMappingDO.isPage()) {
            objectMap.put("totalCount", objectMap.get("total"));
            objectMap.put("rows", objectMap.get("pageResult"));
            objectMap.remove("total");
            objectMap.remove("pageResult");
        }
    }

    private Boolean huaweiRequestSuccess(Map<String, Object> huaweiResponse) {
        String code = String.valueOf(huaweiResponse.get(HUAWEI_RESPONSE_PARAM_CODE));
        if (Objects.equals(HUAWEI_SUCCESS_CODE, code)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 记录调用日志
     *
     * @param url
     * @param httpResult
     * @param requestJson
     * @param mobile
     */
    private void insertApiLog(String url, HttpResult httpResult, String requestJson, String mobile) {
        ThreadPoolUtil.getApiLogPool().execute(() -> {
            try {
                HuaweiApiLogDO apiLogDO = new HuaweiApiLogDO();
                apiLogDO.setTableIndex(SubTableUtils.getTableIndexByMonth());
                apiLogDO.setRequest(requestJson);
                apiLogDO.setMobile(mobile);
                apiLogDO.setUrl(url);
                if (httpResult != null) {
                    apiLogDO.setCostTime(httpResult.getCostTime());
                    apiLogDO.setStatus(httpResult.getCode());
                    if (!StringUtils.isBlank(httpResult.getContent())) {
                        apiLogDO.setResponse(httpResult.getContent());
                    }
                }
                huaweiApiLogMapper.insert(apiLogDO);
            } catch (Exception e) {
                log.error("api log insert error, e:", e);
            }
        });
    }

    private Map<String, Object> getJsonMap(String jsonValue) {
        Map<String, Object> result = null;
        try {
            result = GsonUtils.getJsonMap(jsonValue);
        } catch (Exception e) {
            log.error("[getJsonMap] data error,data:{}", jsonValue, e);
            throw new ApiException(ThirdApiErrorCodes.HUA_WEI_RESPONSE_ERROR);
        }

        return result;
    }

    public static void main(String[] args) {

    }
}
