package com.shinemo.wangge.core.handler;

import com.shinemo.common.tools.http.URLEncodedUtils;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.stallup.domain.params.MaDianParams;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;

/**
 * 码店业务办理
 *
 * @author Chenzhe Mao
 * @date 2020-04-15
 */
public class MaDianAdHandler extends MaDianCommonHandler {

	@Setter
	protected String path;

	private static final String body = "{\"request\":{\"body\":{\"userPhone\":\"%s\",\"adType\":%s},\"header\":{\"appId\":\"%s\"}}}";

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {
		MaDianParams maDianParams = request.getBizParams();
		Integer adType = maDianParams.getAdType();
		String requestBody = String.format(body, request.getUserPhone(), adType, appId);
		StringBuilder url = new StringBuilder();
		url.append(domain)
			.append(path)
			.append("?sign=").append(DigestUtils.md5Hex(requestBody + key))
			.append("&reqdata=").append(URLEncodedUtils.encodePath(requestBody, Charset.forName("utf8")));
		return url.toString();
	}
}
