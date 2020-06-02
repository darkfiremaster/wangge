package com.shinemo.wangge.web.intercepter;

import com.shinemo.smartgrid.domain.SmartGridContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * debug拦截
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
public class DebugInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if(request.getParameter("debug")!=null){
			String mobile = request.getParameter("mobile");
			if (StringUtils.hasText(mobile)) {
				SmartGridContext.setMobile(mobile);
			}
			String uid = request.getParameter("uid");
			if (StringUtils.hasText(uid)) {
				SmartGridContext.setUid(uid);
			}
			String orgId = request.getParameter("orgId");
			if (StringUtils.hasText(orgId)) {
				SmartGridContext.setOrgId(orgId);
			}
			String username = request.getParameter("username");
			if (StringUtils.hasText(username)) {
				SmartGridContext.setUserName(username);
			}
		}
		return true;
	}
}
