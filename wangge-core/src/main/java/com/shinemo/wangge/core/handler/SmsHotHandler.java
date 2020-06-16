package com.shinemo.wangge.core.handler;

import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {

		//String url = domain + path + "?buildingId=xxx&mobile=xxxxxxxxxxxxxx&templateType=xx&authKey=xx";
		String authKey = SmartGridUtils.genUuid();
		log.info("【getUrl】 authKey = {}",authKey);
		String redisValue = request.getUserPhone() + "," + SOURCE_KEY;
		redisService.set(authKey,redisValue,300);
		StringBuilder url = new StringBuilder();
		url.append(domain).
				append(path).
				append("?").
				append("buildingId=").
				append(request.getCommunityId()).
				append("&mobile=").
				append(request.getUserPhone()).
				append("&templateType=").append(1).
				append("&authKey=").append(authKey)
				.append("&activityId=").append("BT_" + request.getActivityId());//todo
		return url.toString();
	}

}
