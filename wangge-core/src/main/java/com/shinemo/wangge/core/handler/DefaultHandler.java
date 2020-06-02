package com.shinemo.wangge.core.handler;


import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;

/**
 * 直接跳转
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
public class DefaultHandler implements UrlRedirectHandler {

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {
		return request.getUrl();
	}
}
