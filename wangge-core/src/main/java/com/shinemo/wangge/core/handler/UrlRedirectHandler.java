package com.shinemo.wangge.core.handler;

import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;

/**
 * url参数拼接处理器
 *
 * @author Chenzhe Mao
 * @date 2020-04-15
 */
public interface UrlRedirectHandler {

	/**
	 * 获取拼好参数的url
	 *
	 * @param request
	 * @return
	 */
	String getUrl(UrlRedirectHandlerRequest request);
}
