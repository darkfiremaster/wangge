package com.shinemo.wangge.web.intercepter;

import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * debug拦截
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
public class DebugInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private SmartGridInfoService smartGridInfoService;

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

			List<GridUserRoleDetail> gridUserRole = smartGridInfoService.getGridUserRole(mobile);
			SmartGridContext.setGridInfo(GsonUtils.toJson(gridUserRole));
			SmartGridContext.setSelectGridInfo(GsonUtils.toJson(gridUserRole.get(0)));
		}
		return true;
	}
}
