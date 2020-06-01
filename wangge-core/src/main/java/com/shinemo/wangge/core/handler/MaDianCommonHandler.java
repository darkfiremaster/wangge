package com.shinemo.wangge.core.handler;

import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import lombok.Setter;

/**
 * 码店
 *
 * @author Chenzhe Mao
 * @date 2020-04-15
 */
public class MaDianCommonHandler implements UrlRedirectHandler {

	@Setter
	protected static String appId;

	@Setter
	protected static String domain;

	@Setter
	protected static String key;

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {
		return null;
	}
}
