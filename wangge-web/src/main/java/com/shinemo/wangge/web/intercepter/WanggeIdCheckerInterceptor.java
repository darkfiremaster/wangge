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

import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.exception.ErrorCode;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.common.GridIdChecker;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.RedisKeyUtil;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * zz
 */
@Component
@Slf4j
public class WanggeIdCheckerInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisService redisService;

    ErrorCode INVALID_MOBILE = new ErrorCode(411, "手机号为空");
    ErrorCode NOT_GRID_USER = new ErrorCode(412, "非网格人员");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Method method = ((HandlerMethod) handler).getMethod();
        final GridIdChecker gridIdChecker = method.getAnnotation(GridIdChecker.class);
        if (null == gridIdChecker) {
            return true;
        }

        String mobile = SmartGridContext.getMobile();
        if (StringUtils.isBlank(mobile)) {
            log.error("[preHandle] mobile is empty");
            throw new ApiException(INVALID_MOBILE);
        }
        String gridInfoCache = redisService.get(RedisKeyUtil.getUserGridInfoPrefixKey(mobile));
        if (StringUtils.isBlank(gridInfoCache)) {
            log.error("[preHandle] user not grid user,mobile = {}", mobile);
            throw new ApiException(NOT_GRID_USER);
        }
        return true;
    }
}
