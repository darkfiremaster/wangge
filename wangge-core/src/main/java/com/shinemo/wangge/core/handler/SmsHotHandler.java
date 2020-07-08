package com.shinemo.wangge.core.handler;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短信预热
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class SmsHotHandler implements UrlRedirectHandler{

	/** 域名 */
	@Setter
	@Getter
	private String domain;

	/** 路径 */
	@Setter
	@Getter
	private String path;


	private static final String SOURCE_KEY = "sms-warm-up-10001";

	@Setter
	private RedisService redisService;

	@NacosValue(value = "${huawei.smshot.url}", autoRefreshed = true)
	private String smsHotUrl;
	@NacosValue(value = "${yujing.seed}", autoRefreshed = true)
	private String seed;

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {

		Map<String, String> map = new LinkedHashMap<>();
		map.put(request.getCommunityId(),request.getCommunityName());

		long timestamp = System.currentTimeMillis();
		Map<String, Object> formData = new HashMap<>();
		formData.put("mobile", SmartGridContext.getMobile());
		formData.put("gridId", SmartGridContext.getSelectGridUserRoleDetail().getId());
		formData.put("gridName", SmartGridContext.getSelectGridUserRoleDetail().getName());
		formData.put("timestamp", timestamp);
		formData.put("building", map);
		formData.put("prehotObjectType", 1);

		log.info("[SmsHotHandler] 请求参数formData:{}", formData);
		String paramStr = EncryptUtil.buildParameterString(formData);

		//1、加密
		String encryptData = EncryptUtil.encrypt(paramStr, seed);

		//2、生成签名
		String sign = Md5Util.getMD5Str(encryptData + "," + seed + "," + timestamp);

		String url = smsHotUrl + "?";

		StringBuilder sb = new StringBuilder(url);
		sb.append("paramData=").append(encryptData)
				.append("&timestamp=").append(timestamp)
				.append("&sign=").append(sign);

		String smsHotUrl = sb.toString();
		log.info("[SmsHotHandler]非摆摊计划场景,生成短信预热url:{}", smsHotUrl);

		return smsHotUrl;
	}

}
