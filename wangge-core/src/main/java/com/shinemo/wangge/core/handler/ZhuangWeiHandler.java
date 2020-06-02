package com.shinemo.wangge.core.handler;

import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.params.ZhuangWeiParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 装维
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class ZhuangWeiHandler implements UrlRedirectHandler {

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {

		ZhuangWeiParams params = request.getBizParams();

		Map<String, Object> formData = new HashMap<>();
		formData.put("downloadUrl", params.getDownloadUrl() + "?timestamp=" + System.currentTimeMillis());

		return GsonUtils.toJson(formData);
	}
}
