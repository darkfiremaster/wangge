package com.shinemo.wangge.core.handler;

import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import lombok.Setter;

/**
 * 督导
 *
 * @author Chenzhe Mao
 * @date 2020-04-15
 */
public class DuDaoCommonHandler implements UrlRedirectHandler {

	@Setter
	protected static String domain;

	@Setter
	protected static String seed;

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {
		return null;
	}
}
