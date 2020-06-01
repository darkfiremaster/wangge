package com.shinemo.wangge.core.handler;

import com.shinemo.common.tools.http.URLEncodedUtils;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

/**
 * 码店智慧查询
 *
 * @author Chenzhe Mao
 * @date 2020-04-15
 */
public class MaDianIntelligentHandler extends MaDianCommonHandler {

	@Setter
	protected String path;

	private static final String body = "{\"request\":{\"body\":{\"userPhone\":\"%s\",\"mobile\":\"%s\"},\"header\":{\"appId\":\"%s\"}}}";

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {
		String queryMobile = request.getQueryMobile();
		String requestBody = String.format(body, request.getUserPhone(), StringUtils.hasText(queryMobile) ? queryMobile : "", appId);
		StringBuilder url = new StringBuilder();
		url.append(domain)
			.append(path)
			.append("?sign=").append(DigestUtils.md5Hex(requestBody + key))
			.append("&reqdata=").append(URLEncodedUtils.encodePath(requestBody, Charset.forName("utf8")));
		return url.toString();
	}

}
