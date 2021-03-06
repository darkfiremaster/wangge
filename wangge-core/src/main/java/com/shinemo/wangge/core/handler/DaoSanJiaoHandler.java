package com.shinemo.wangge.core.handler;

import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 倒三角
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class DaoSanJiaoHandler implements UrlRedirectHandler{

	/** 域名 */
	@Setter
	@Getter
	private String domain;

	/** 路径 */
	@Setter
	@Getter
	private String path;

	@Setter
	@Getter
	private Integer sid;

	@Setter
	@Getter
	private String seed;

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {

		String mobile = request.getUserPhone();
		long timestamp = System.currentTimeMillis();

		Map<String, Object> formData = new HashMap<>();
		formData.put("mobile", mobile);
		formData.put("sid", sid);
		formData.put("timestamp", timestamp);

		String paramStr = EncryptUtil.buildParameterString(formData);

		//1、加密
		String encryptData = EncryptUtil.encrypt(paramStr, seed);

		//2、生成签名
		String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);

		String url = domain + path + "?";

		StringBuilder sb = new StringBuilder(url);
		sb.append("paramData=").append(encryptData)
				.append("&timestamp=").append(timestamp)
				.append("&sign=").append(sign);

		return sb.toString();
	}
}
