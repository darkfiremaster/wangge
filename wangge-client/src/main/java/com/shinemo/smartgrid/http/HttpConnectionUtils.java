package com.shinemo.smartgrid.http;

import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HttpConnectionUtils {
    private static final Logger log = LoggerFactory.getLogger("http");

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(false)
            .connectTimeout(HttpConstants.CONN_TIMEOUT_SECONDS, TimeUnit.SECONDS) // 连接超时
            .readTimeout(HttpConstants.READ_TIME_OUT_SECONDS, TimeUnit.SECONDS) // 读取超时
            .writeTimeout(HttpConstants.WRITE_TIME_OUT_SECONDS, TimeUnit.SECONDS) // 写超时
            .addNetworkInterceptor(new NetInterceptor()).build();
    }
    public static <T> T httpPost(String url, String content, Map<String, Object> headParams, Class<T> clazz) {
        HttpResult result = httpPost(url, content, headParams);
        return httpPost(url, result, v -> {
            try {
                return v.getResult(clazz);
            } catch (Exception e) {
                log.error("httpPost url:{},content:{},error: ", url, content, e);
            }
            return null;
        });
    }

    public static <T> T httpPost(String url, String content, Class<T> clazz) {
        return httpPost(url, content, null, clazz);
    }

    public static <T> T httpPost(String url, HttpResult result, Function<HttpResult, T> function) {
        if (result != null && result.success()) {
            return function.apply(result);
        } else {
            if (result == null) {
                log.error("httpPost {} result is null", url);
            } else {
                log.error("httpPost {} error,code:{},content:{}", url, result.getCode(), result.getContent());
            }
        }
        return null;
    }

    public static HttpResult httpPost(String url, String content, Map<String, Object> headParams) {
        Request.Builder builder = new Request.Builder().url(url)
                .post(RequestBody.create(MediaType.parse("application" + "/json"), content));
        if (!CollectionUtils.isEmpty(headParams)) {
            headParams.forEach((k, v) -> {
                if (StringUtils.isNotBlank(k) && v != null) {
                    builder.header(k, v.toString());
                }
            });
        }
        long start = System.currentTimeMillis();
        Response response = null;
        try {
            response = okHttpClient.newCall(builder.build()).execute();
        } catch (Exception e) {
            log.error("http post url:{},content:{},headParams:{},error: ", url, content, headParams, e);
        }
        long end = System.currentTimeMillis();
        long costTime = end - start;

        if (costTime >= 500) {
            if (response == null) {
                log.info("http - [httpPost] url: {}, code: {}, costTime: {}", url, 0, costTime);
            } else {
                log.info("http - [httpPost] url: {}, code: {}, costTime: {}", url, response.code(), costTime);
            }
        }

        if (response != null) {
            log.info("[httpPost] url: {}, code: {}, costTime: {}.", url, response.code(), costTime);

            if (response.isSuccessful()) {
                try {
                    String responseContent = response.body().string();
                    log.info("httpPost url:{},content:{},headparams:{},costTime:{},response:{}", url, content,
                            headParams, costTime, responseContent);
                    return HttpResult.builder().code(response.code()).content(responseContent).costTime(costTime)
                            .build();
                } catch (IOException e) {
                    log.error("http post exception url:{},costTime:{},response:{},response.body to string error: ", url,
                            costTime, response, e);
                    return HttpResult.builder().code(response.code()).costTime(costTime).build();
                }
            } else {
                log.info("httpPost url:{},content:{},headparams:{},costTime:{},response:{}", url, content, headParams,
                        costTime, response);
                return HttpResult.builder().code(response.code()).costTime(costTime).build();
            }
        }
        log.error("http post response is null,url:{},content:{},headParams:{},costTime:{}", url, content, headParams,
                costTime);
        return HttpResult.builder().code(500).costTime(costTime).build();
    }

    public static HttpResult httpPost(String url, String contentType, byte[] bytes, Map<String, Object> headParams) {
        Request.Builder builder = new Request.Builder().url(url)
                .post(RequestBody.create(MediaType.parse(contentType), bytes));
        if (!CollectionUtils.isEmpty(headParams)) {
            headParams.forEach((k, v) -> {
                if (StringUtils.isNotBlank(k) && v != null) {
                    builder.header(k, v.toString());
                }
            });
        }
        long start = System.currentTimeMillis();
        Response response = null;
        try {
            response = okHttpClient.newCall(builder.build()).execute();
        } catch (Exception e) {
            log.error("http post url:{},headParams:{},error: ", url, headParams, e);
        }
        long end = System.currentTimeMillis();
        long costTime = end - start;
        if (response != null) {
            log.info("[httpPost] url: {}, code: {}, costTime: {}.", url, response.code(), costTime);
            if (response.isSuccessful()) {
                try {
                    String responseContent = response.body().string();
                    log.info("httpPost url:{},headparams:{},costTime:{}, response:{}", url, headParams, costTime,
                            responseContent);
                    return HttpResult.builder().code(response.code()).content(responseContent).costTime(costTime)
                            .build();
                } catch (IOException e) {
                    log.error("http get url:{},costTime:{},response:{},response.body to string error: ", url, costTime,
                            response, e);
                    return HttpResult.builder().code(response.code()).costTime(costTime).build();
                }
            } else {
                log.info("httpPost url:{},headparams:{},costTime:{},response:{}", url, headParams, costTime, response);
                return HttpResult.builder().code(response.code()).costTime(costTime).build();
            }
        }
        log.error("http post response is null,url:{},headParams:{},costTime:{}", url, headParams, costTime);
        return HttpResult.builder().code(500).costTime(costTime).build();
    }

    public static HttpResult get(String url, Map<String, Object> params, Map<String, Object> headParams) {
        StringBuffer sb = new StringBuffer(url);
        if (!CollectionUtils.isEmpty(params)) {
            sb.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue())))
                        .append("&");
            }
            if (sb.indexOf("&") > -1) {
                url = sb.substring(0, sb.lastIndexOf("&"));
            }
        }
        Request.Builder builder = new Request.Builder().url(url).get();
        if (!CollectionUtils.isEmpty(headParams)) {
            headParams.forEach((k, v) -> {
                if (StringUtils.isNotBlank(k) && v != null) {
                    builder.header(k, v.toString());
                }
            });
        }
        long start = System.currentTimeMillis();
        Response response = null;
        try {
            response = okHttpClient.newCall(builder.build()).execute();
        } catch (Exception e) {
            log.error("http get url:{},error: ", url, e);
        }
        long end = System.currentTimeMillis();
        long costTime = end - start;
        if (response != null) {
            log.info("[httpGet] url: {}, code: {}, costTime: {}.", url, response.code(), costTime);
            if (response.isSuccessful()) {
                try {
                    String responseContent = response.body().string();
                    log.info("http get url:{},headparams:{},costTime:{},response:{}", url, headParams, costTime,
                            responseContent);
                    return HttpResult.builder().code(response.code()).content(responseContent).costTime(costTime)
                            .build();
                } catch (IOException e) {
                    log.error("http get url:{},costTime:{},response:{},response.body to string error: ", url, costTime,
                            response, e);
                    return HttpResult.builder().code(response.code()).costTime(costTime).build();
                }
            } else {
                log.info("http get url:{},headparams:{},costTime:{},response:{}", url, headParams, costTime, response);
                return HttpResult.builder().code(response.code()).costTime(costTime).build();
            }
        }
        log.error("http get response is null,url:{},headParams:{},costTime:{},params:{}", url, headParams, costTime,
                params);
        return HttpResult.builder().code(500).costTime(costTime).build();
    }

    public static byte[] download(String url, Map<String, Object> params, Map<String, Object> headParams) {
        StringBuffer sb = new StringBuffer(url);
        if (!CollectionUtils.isEmpty(params)) {
            sb.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue())))
                        .append("&");
            }
        }
        if (sb.indexOf("&") > -1) {
            url = sb.substring(0, sb.lastIndexOf("&"));
        }
        Request.Builder builder = new Request.Builder().url(url).get();
        if (!CollectionUtils.isEmpty(headParams)) {
            headParams.forEach((k, v) -> {
                if (StringUtils.isNotBlank(k) && v != null) {
                    builder.header(k, v.toString());
                }
            });
        }
        long start = System.currentTimeMillis();
        Response response = null;
        try {
            response = okHttpClient.newCall(builder.build()).execute();
        } catch (Exception e) {
            log.error("http download url:{},error: ", url, e);
        }
        long end = System.currentTimeMillis();
        long costTime = end - start;
        log.info("http download url:{},headparams:{},costTime:{}", url, headParams, costTime);
        if (response != null) {
            log.info("[httpDownload] url: {}, code: {}, costTime: {}.", url, response.code(), costTime);
            if (response.isSuccessful()) {
                try {
                    log.info("http download url:{},headparams:{}", url, headParams);
                    return response.body().bytes();
                } catch (IOException e) {
                    log.error("http download url:{},response:{},response.body to string error: ", url, response, e);
                }
            } else {
                log.info("http download url:{},headparams:{},response:{}", url, headParams, response);
            }
        }
        log.error("http download response is null,url:{},headParams:{},params:{}", url, headParams, params);
        return null;
    }

    public static <T> T httpGet(String url, Class<T> clazz, Map<String, Object> params,
                                Map<String, Object> headParams) {
        HttpResult result = get(url, params, headParams);
        return httpGet(url, result, v -> {
            try {
                return result.getResult(clazz);
            } catch (Exception e) {
                log.error("httpGet url:{},params:{},headParams:{},error: ", url, params, headParams, e);
            }
            return null;
        });
    }

    public static <T> T httpGet(String url, Class<T> clazz, Map<String, Object> params) {
        return httpGet(url, clazz, params, null);
    }

    public static <T> T httpGet(String url, Class<T> clazz) {
        return httpGet(url, clazz, null);
    }

    public static <T> T httpGet(String url, HttpResult result, Function<HttpResult, T> function) {
        if (result != null && result.success()) {
            return function.apply(result);
        } else {
            if (result == null) {
                log.error("httpGet {} result is null", url);
            } else {
                log.error("httpGet {} error,code:{},content:{}", url, result.getCode(), result.getContent());
            }
        }
        return null;
    }
}

