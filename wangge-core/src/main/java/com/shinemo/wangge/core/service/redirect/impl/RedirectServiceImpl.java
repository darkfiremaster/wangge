package com.shinemo.wangge.core.service.redirect.impl;

import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.wangge.core.config.properties.ZhuangweiPropertity;
import com.shinemo.wangge.core.service.redirect.RedirectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/7/15 16:43
 * @Desc
 */
@Service
@Slf4j
public class RedirectServiceImpl implements RedirectService {

    @Resource
    private ZhuangweiPropertity zhuangweiPropertity;

    @Override
    public ApiResult<String> getRedirectUrl(Integer type) {
        if (type == 1) {
            return getZhuangyiDataBroadUrl();
        }
        throw new ApiException("type类型错误");
    }

    private ApiResult<String> getZhuangyiDataBroadUrl() {
        String seed = zhuangweiPropertity.getSeed();
        String domain = zhuangweiPropertity.getDomain();
        String path = zhuangweiPropertity.getDataBoardUrl();
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobileTel", SmartGridContext.getMobile());
        formData.put("gridName", SmartGridContext.getSelectGridUserRoleDetail().getName());
        formData.put("areaName", SmartGridContext.getSelectGridUserRoleDetail().getCityName());
        formData.put("roleName", SmartGridContext.getSelectGridUserRoleDetail().getRoleList().get(0).getName());
        formData.put("timestamp", timestamp);
        String paramData = EncryptUtil.buildParameterString(formData, Boolean.FALSE);
        try {
            log.info("[getZhuangyiDataBroadUrl] 加密前参数paramData:{}", URLDecoder.decode(paramData,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramData, seed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

        String url = domain + path + "?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign).toString();


        log.info("[getZhuangyiDataBroadUrl] 生成装移数据看板跳转url:{}", url);
        return ApiResult.of(0, url);
    }
}
