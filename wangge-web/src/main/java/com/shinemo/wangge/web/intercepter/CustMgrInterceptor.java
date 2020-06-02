package com.shinemo.wangge.web.intercepter;

import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.LoginContext;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.jace.api.cmmc.base.CmmcBaseClient;
import com.shinemo.jace.comm.JaceMutableLong;
import com.shinemo.jace.comm.JaceMutableString;
import com.shinemo.jace.comm.JaceRetCode;
import com.shinemo.smartgrid.helper.OptionConfig;
import com.shinemo.smartgrid.helper.UserLoginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author: luchao
 * @email: luc@shinemo.com
 * @date: 2019/7/19
 */
@Component
public class CustMgrInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger("access.log");

    @Resource
    private CmmcBaseClient cmmcBaseClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        final Method method = ((HandlerMethod) handler).getMethod();
        final SmIgnore smIgnore = method.getAnnotation(SmIgnore.class);
        if (null != smIgnore) {
            return true;
        }
        String uid = LoginContext.getUid();
        String orgId = LoginContext.getOrgId();
        String orgName = LoginContext.getOrgName();
        JaceMutableString name = new JaceMutableString();
        JaceMutableString mobile = new JaceMutableString();
        JaceMutableString regionCodes = new JaceMutableString();
        JaceMutableLong mgrId = new JaceMutableLong(0);
        int rc = cmmcBaseClient.getCmSyncMgrByUid(uid, name, mobile, regionCodes);
        if (rc != JaceRetCode.RET_SUCCESS) {
            logger.error("cmmcBaseClient getCmSyncMgrByUid fail - rc:{},uid:{},orgId:{},orgName:{}", rc, uid, orgId, orgName);
            response.setStatus(200);
            response.getWriter().println(ApiResult.fail("fail", OptionConfig.SERVER_ERROR).toJson());
            return false;
        }
        UserLoginHelper.set(name.get(), mobile.get(), mgrId.get(), regionCodes.get());
        logger.info("cmmcBaseClient getCmSyncMgrByUid success - uid:{},orgId:{},orgName:{},name:{},mobile:{},mgrId:{},regionCodes:{}", uid, orgId, orgName, name.get(), mobile.get(), mgrId.get(), regionCodes.get());
        return true;
    }
}
