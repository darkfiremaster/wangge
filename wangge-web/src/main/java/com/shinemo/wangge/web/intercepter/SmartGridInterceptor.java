package com.shinemo.wangge.web.intercepter;

import static com.shinemo.Aace.RetCode.RET_SUCCESS;
import static com.shinemo.util.WebUtils.getValueFromCookies;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shinemo.common.tools.Jsons;
import com.shinemo.common.tools.LoginContext;
import com.shinemo.common.tools.Utils;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.log.Logs;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.model.BackdoorLoginDO;
import com.shinemo.smartgrid.domain.query.BackdoorLoginQuery;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.RedisKeyUtil;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.user.UserService;
import com.shinemo.wangge.dal.mapper.BackdoorLoginMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * debug拦截
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Slf4j
@Component
public class SmartGridInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisService redisService;

    @Resource
    private HuaWeiService huaWeiService;

    @Resource
    private UserService userService;

    @Resource
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Resource
    private BackdoorLoginMapper backdoorLoginMapper;

    public static final int EXPIRE_TIME = 60 * 60 * 24;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // todo 线上去除
        checkAuth(request);

        Cookie[] cookies = request.getCookies();
        String uid = LoginContext.getUid();
        String orgId = LoginContext.getOrgId();
        String orgName = LoginContext.getOrgName();
        String mobile = LoginContext.getMobile();
        String userName = LoginContext.getUserName();
        if (mobile == null) {
            mobile = getValueFromCookies("mobile", cookies);
            if (mobile == null) {
                // 从debugInterceptor中获取到的手机号
                mobile = SmartGridContext.getMobile();
            }
        }
        if (mobile == null) {
            log.error("[preHandle] mobile is null");
            return true;
        }

        BackdoorLoginQuery qbl = new BackdoorLoginQuery();
        qbl.setMobile(mobile);
        BackdoorLoginDO bl = backdoorLoginMapper.get(qbl);
        if (bl != null) {
            uid = bl.getCUid();
            orgId = bl.getCOrgId();
            orgName = bl.getCOrgName();
            mobile = bl.getCMobile();
            userName = bl.getCUserName();
        }

        if (uid == null) {
            uid = getValueFromCookies("userId", cookies);
            if (uid == null) {
                uid = getValueFromCookies("uid", cookies);
            }
            if (uid == null) {
                // 从debugInterceptor中获取到uid
                uid = SmartGridContext.getUid();
            }

        }
        if (uid != null) {
            SmartGridContext.setUid(uid);
        }
        if (orgId == null) {
            orgId = getValueFromCookies("orgId", cookies);
        }
        if (orgId != null) {
            SmartGridContext.setOrgId(orgId);
        }
        if (orgName == null) {
            orgName = getValueFromCookies("orgName", cookies);
        }
        if (orgName != null) {
            SmartGridContext.setOrgName(orgName);
        }
        if (mobile != null) {
            SmartGridContext.setMobile(mobile);
        }

        if (userName == null) {
            userName = getValueFromCookies("username", cookies);
        }
        if (userName != null) {
            SmartGridContext.setUserName(userName);
        }
        // 查询用户网格信息,并更新
        String gridInfoCache = redisService.get(RedisKeyUtil.getUserGridInfoPrefixKey(mobile));
        if (StringUtils.isBlank(gridInfoCache)) {
            log.info("[preHandle] gridInfoCache is null, start query gridinfo,mobile:{}", mobile);
            List<GridUserRoleDetail> gridUserRole = getGridUserRole(mobile);
            if (CollectionUtils.isEmpty(gridUserRole)) {
                log.info("[preHandle] gridUserRoleList is null,mobile:{}", mobile);
                return true;
            }

            // 更新用户网格角色关系
            log.info("[preHandle]  start update gridinfo,mobile:{}", mobile);
            String finalMobile = mobile;
            asyncServiceExecutor.submit(() -> {
                userService.updateUserGridRoleRelation(gridUserRole, finalMobile);
            });

            gridInfoCache = GsonUtils.toJson(gridUserRole);
            redisService.set(RedisKeyUtil.getUserGridInfoPrefixKey(mobile), gridInfoCache, EXPIRE_TIME);
        }

        SmartGridContext.setGridInfo(gridInfoCache);
        return true;
    }

    private List<GridUserRoleDetail> getGridUserRole(String mobile) {
        HuaWeiRequest huaWeiRequest = HuaWeiRequest.builder().mobile(mobile).build();
        try {
            ApiResult<List<GridUserRoleDetail>> apiResult = huaWeiService.getGridUserInfo(huaWeiRequest);
            if (!apiResult.isSuccess()) {
                log.error("[getGridUserRole] huaWeiService.getGridUserInfo not success,apiResult = {}," + "mobile = {}",
                        apiResult, mobile);
                return null;
            }
            return apiResult.getData();
        } catch (ApiException e) {
            log.error("[getGridUserRole] huaWeiService.getGridUserInfo error,msg = {},mobile = {}", e.getMessage(),
                    mobile);
            return null;
        }
    }

    public boolean checkAuth(HttpServletRequest request) {
        String uid = null;
        String token = null;
        long timestamp = 0;
        String orgId = null;
        String userInfo = null;
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            token = getValueFromCookies("token", cookies);
            if (token == null) {
                token = getValueFromCookies("ticket", cookies);
            }

            timestamp = NumberUtils.toLong(getValueFromCookies("timeStamp", cookies));
            if (timestamp == 0) {
                timestamp = NumberUtils.toLong(getValueFromCookies("ts", cookies));
            }

            uid = getValueFromCookies("userId", cookies);

            if (uid == null) {
                uid = getValueFromCookies("uid", cookies);
            }

            orgId = getValueFromCookies("orgId", cookies);
            userInfo = getValueFromCookies("userInfo", cookies);
        }

        if (token == null || uid == null) {
            token = request.getParameter("token");
            timestamp = NumberUtils.toLong(request.getParameter("timeStamp"));
            uid = request.getParameter("userId");
        }

        if (token == null || uid == null) {
            String json = request.getHeader("token");
            if (json != null) {
                Map<String, String> map = Jsons.fromJson(json, new TypeReference<Map<String, String>>() {
                });
                if (map != null) {
                    token = map.get("token");
                    timestamp = NumberUtils.toLong(map.get("ts"));
                    uid = map.get("uid");
                    orgId = map.get("orgId");
                }
            }
        }

        if (StringUtils.isBlank(token) || StringUtils.isBlank(uid)) {
            Logs.error("check token fail, token or uid from cookies is not allow blank");
            return false;
        }

        int ret = RET_SUCCESS;
        // MutableBoolean isSuccess = new MutableBoolean();
        // try {
        // ret = imLoginClient.verifyToken(uid, token, timestamp, isSuccess);
        // } catch (Exception ex) {
        // Logs.error("check token fail, ret={}, token={}", ret, token, ex);
        // }

        if (ret == RET_SUCCESS) {
            // 特殊场景下cookie里没有orgId
            if (Utils.isEmpty(orgId)) {
                orgId = request.getParameter("orgId");
            }

            if (Utils.isNotEmpty(userInfo)) {
                Map<String, ?> map = Jsons.fromJson(Utils.decodeUrl(userInfo), Map.class);
                if (Utils.isNotEmpty(map)) {
                    String[] keys = new String[] { "orgId", "mobile", "orgName", "name" };
                    for (String key : keys) {
                        Object value = map.get(key);
                        if (value != null) {
                            LoginContext.put(key, value);
                        }
                    }
                }
            }

            LoginContext.setUid(uid);
            if (orgId != null) {
                LoginContext.setOrgId(orgId);
            }
            return true;
        }

        Logs.error("token token fail, uid:{}, token:{}, timestamp:{}, retCode:{}", uid, token, timestamp, ret);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SmartGridContext.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
