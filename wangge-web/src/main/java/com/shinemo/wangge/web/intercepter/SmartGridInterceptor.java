package com.shinemo.wangge.web.intercepter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shinemo.common.tools.Jsons;
import com.shinemo.common.tools.LoginContext;
import com.shinemo.common.tools.Utils;
import com.shinemo.common.tools.log.Logs;
import com.shinemo.smartgrid.domain.SmartGridContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.shinemo.Aace.RetCode.RET_SUCCESS;
import static com.shinemo.util.WebUtils.getValueFromCookies;

/**
 * debug拦截
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Slf4j
public class SmartGridInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		//todo 线上去除
		checkAuth(request);

		Cookie[] cookies = request.getCookies();
		String uid = LoginContext.getUid();
		String orgId = LoginContext.getOrgId();
		String orgName = LoginContext.getOrgName();
		String mobile = LoginContext.getMobile();
		String userName = LoginContext.getUserName();
		if (uid == null) {
			uid = getValueFromCookies("userId", cookies);
			if (uid == null) {
				uid = getValueFromCookies("uid", cookies);
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
		if (mobile == null) {
			mobile = getValueFromCookies("mobile", cookies);
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
		return true;
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
//        MutableBoolean isSuccess = new MutableBoolean();
//        try {
//            ret = imLoginClient.verifyToken(uid, token, timestamp, isSuccess);
//        } catch (Exception ex) {
//            Logs.error("check token fail, ret={}, token={}", ret, token, ex);
//        }

		if (ret == RET_SUCCESS) {
			//特殊场景下cookie里没有orgId
			if (Utils.isEmpty(orgId)) {
				orgId = request.getParameter("orgId");
			}

			if (Utils.isNotEmpty(userInfo)) {
				Map<String, ?> map = Jsons.fromJson(Utils.decodeUrl(userInfo), Map.class);
				if (Utils.isNotEmpty(map)) {
					String[] keys = new String[]{"orgId", "mobile", "orgName", "name"};
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
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		SmartGridContext.remove();
		super.afterCompletion(request, response, handler, ex);
	}
}
