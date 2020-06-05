/*
 * (C) Copyright 2015-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */

package com.shinemo.wangge.web.intercepter;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shinemo.Aace.MutableBoolean;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Imlogin.IMLoginService;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.client.util.WebUtil;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.Jsons;
import com.shinemo.common.tools.LoginContext;
import com.shinemo.common.tools.Utils;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.common.GridIdChecker;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shinemo.Aace.RetCode.RET_SUCCESS;
import static com.shinemo.util.WebUtils.getValueFromCookies;

/**
 * zz
 */
@Component
@Slf4j
public class WanggeIdCheckerInterceptor extends HandlerInterceptorAdapter {


    @NacosValue(value = "${domain}", autoRefreshed = true)
    private String domain = "127.0.0.1";
    @Resource
    private HuaWeiService huaWeiService;

    public static final int EXPIRE_TIME = 30 * 60 * 60 * 24;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Method method = ((HandlerMethod) handler).getMethod();
        final GridIdChecker gridIdChecker = method.getAnnotation(GridIdChecker.class);
        if (null == gridIdChecker) {
            return true;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            log.error("wanggeIdCheck check fail, cookies is empty!");
            return false;
        }
        String gridInfo = getValueFromCookies("gridInfo", cookies);

        if (StringUtils.isBlank(gridInfo)) {
            String mobile = SmartGridContext.getMobile();
            if (StringUtils.isBlank(mobile)) {
                log.error("[WanggeIdCheckerInterceptor] mobile is empty");
                return false;
            }
            List<GridUserRoleDetail> gridUserRole = getGridUserRole(mobile);
            if (CollectionUtils.isEmpty(gridUserRole)) {
                log.error("[WanggeIdCheckerInterceptor] user not grid user,mobile = {}",mobile);
                return false;
            }
            String json = GsonUtils.toJson(gridUserRole);
            String encodeCookie = null;
            try {
                encodeCookie = URLEncoder.encode( json,"utf-8");
            } catch (UnsupportedEncodingException e) {
                log.error("[WanggeIdCheckerInterceptor] URLEncoder error,gridUserRole = {}",json);
                return false;
            }
            SmartGridContext.setGridInfo(json);
            WebUtil.addCookie(request, response, "gridInfo", encodeCookie,
                    domain, "/", EXPIRE_TIME, false);
            return true;
        }

        String decode = null;
        try {
            decode = URLDecoder.decode(gridInfo, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("[WanggeIdCheckerInterceptor] URLDecoder error,gridUserRole = {}",gridInfo);
            return false;
        }
        SmartGridContext.setGridInfo(decode);
        return true;
    }

    private List<GridUserRoleDetail> getGridUserRole(String mobile) {
        HuaWeiRequest huaWeiRequest = HuaWeiRequest.builder().mobile(mobile).build();
        try {
            ApiResult<List<GridUserRoleDetail>> apiResult = huaWeiService.getGridUserInfo(huaWeiRequest);
            if (!apiResult.isSuccess()) {
                log.error("[getGridUserRole] huaWeiService.getGridUserInfo not success,apiResult = {}," +
                        "mobile = {}",apiResult,mobile);
                return null;
            }
            return apiResult.getData();
        }catch (ApiException e) {
            log.error("[getGridUserRole] huaWeiService.getGridUserInfo error,msg = {},mobile = {}",e.getLogMsg(),mobile);
            return null;
        }
    }
}
