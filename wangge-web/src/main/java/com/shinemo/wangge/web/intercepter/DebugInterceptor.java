package com.shinemo.wangge.web.intercepter;

import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * debug拦截
 *
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
@Component
@Slf4j
public class DebugInterceptor extends HandlerInterceptorAdapter {

	@Resource
	private SmartGridInfoService smartGridInfoService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if(request.getParameter("debug")!=null){
			log.info("[DebugInterceptor] 进入debug模式");
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

			if (!StringUtils.isEmpty(mobile)) {
				List<GridUserRoleDetail> gridUserRole = smartGridInfoService.getGridUserRole(mobile);
				if (!CollectionUtils.isEmpty(gridUserRole)) {
					SmartGridContext.setGridInfo(GsonUtils.toJson(gridUserRole));
					SmartGridContext.setSelectGridInfo(GsonUtils.toJson(gridUserRole.get(0)));
				}
			}

		}
		return true;
	}
}
