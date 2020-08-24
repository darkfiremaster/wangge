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

import static com.shinemo.Aace.RetCode.RET_SUCCESS;
import static com.shinemo.common.tools.exception.CommonErrorCodes.INVALID_TOKEN;
import static com.shinemo.util.WebUtils.getValueFromCookies;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.shinemo.Aace.MutableBoolean;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Imlogin.IMLoginService;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.common.tools.LoginContext;
import com.shinemo.common.tools.Utils;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.smartgrid.utils.GsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ohun on 2019-05-21.
 *
 * @author ohun@live.cn (夜色)
 */
@Component
@Slf4j
public class TokenAuthChecker extends HandlerInterceptorAdapter {

    @Resource
    private IMLoginService aaceIMLoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return  true;
//        String uid = null;
//        String token = null;
//        long timestamp = 0;
//        String orgId = null;
//        Cookie[] cookies = request.getCookies();
//        if (null != cookies) {
//            token = getValueFromCookies("token", cookies);
//            if (token == null) {
//                token = getValueFromCookies("ticket", cookies);
//            }
//
//            timestamp = NumberUtils.toLong(getValueFromCookies("timeStamp", cookies));
//            if (timestamp == 0) {
//                timestamp = NumberUtils.toLong(getValueFromCookies("ts", cookies));
//            }
//
//            uid = getValueFromCookies("userId", cookies);
//
//            if (uid == null) {
//                uid = getValueFromCookies("uid", cookies);
//            }
//
//            orgId = getValueFromCookies("orgId", cookies);
//        }
//
//        if (token == null || uid == null) {
//            token = request.getParameter("token");
//            timestamp = NumberUtils.toLong(request.getParameter("timeStamp"));
//            uid = request.getParameter("userId");
//        }
//
//        if (token == null || uid == null) {
//            String json = request.getHeader("token");
//            if (json != null) {
//                Map<String, String> map = GsonUtils.getStringMap(json);
//                if (map != null) {
//                    token = map.get("token");
//                    timestamp = NumberUtils.toLong(map.get("ts"));
//                    uid = map.get("uid");
//                    orgId = map.get("orgId");
//                }
//            }
//        }
//
//        if (StringUtils.isBlank(token) || StringUtils.isBlank(uid)) {
//            log.error("check token fail, token or uid from cookies is not allow blank");
//            throw new ApiException(INVALID_TOKEN);
//        }
//
//        int ret = RET_SUCCESS;
//        MutableBoolean isSuccess = new MutableBoolean();
//        AaceContext ctx = new AaceContext(AppTypeEnum.GUANGXI.getId() + "");
//        ctx.set("uid", uid + "");
//        try {
//            ret = aaceIMLoginService.verifyToken(uid, token, timestamp, isSuccess, ctx);
//        } catch (Exception ex) {
//            log.error("check token fail, ret={}, token={}", ret, token, ex);
//        }
//
//        if (ret == RET_SUCCESS) {
//            // 特殊场景下cookie里没有orgId
//            if (Utils.isEmpty(orgId)) {
//                orgId = request.getParameter("orgId");
//            }
//
//            LoginContext.setUid(uid);
//            if (orgId != null) {
//                LoginContext.setOrgId(orgId);
//            }
//
//            return true;
//        }
//
//        log.error("token token fail, uid:{}, token:{}, timestamp:{}, retCode:{}", uid, token, timestamp, ret);
//        throw new ApiException(INVALID_TOKEN);
    }
}
