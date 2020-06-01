package com.shinemo.wangge.core.handler;

import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.stallup.domain.params.DuDaoParams;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 督导h5
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class DuDaoHandler extends DuDaoCommonHandler {
	/**
	 * 路径
	 */
	@Setter
	@Getter
	private String path;

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {

		String mobile = request.getUserPhone();
		DuDaoParams duDaoParams = request.getBizParams();
		Integer resId = duDaoParams == null ? null : duDaoParams.getResId();
		long timestamp = System.currentTimeMillis();
		Map<String, Object> formData = new HashMap<>();
		formData.put("mobile", mobile);
		formData.put("urlType", "bgcy_app");
		formData.put("timestamp", timestamp);
		if (resId != null) {
			formData.put("resId", resId);
		}
		String paramStr = EncryptUtil.buildParameterString(formData);
		//1、加密
		String encryptData = EncryptUtil.encrypt(paramStr, seed);

		//2、生成签名
		String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

		StringBuilder sb = new StringBuilder();
		sb.append(domain)
			.append(path)
			.append("paramData=").append(encryptData)
			.append("&timestamp=").append(timestamp)
			.append("&sign=").append(sign);

		return sb.toString();
	}
}
