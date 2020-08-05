package com.shinemo.wangge.core.handler;

import cn.hutool.core.util.StrUtil;
import com.shinemo.smartgrid.domain.UrlRedirectHandlerRequest;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.params.SuiShenXingParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 随身行
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class SuiShenXingHandler implements UrlRedirectHandler {

	@Override
	public String getUrl(UrlRedirectHandlerRequest request) {

		SuiShenXingParams params = request.getBizParams();

		Map<String, Object> formData = new HashMap<>();
		formData.put("downloadUrl", params.getDownloadUrl() + "?timestamp=" + System.currentTimeMillis());
		if (StrUtil.isNotBlank(params.getBusiCode())) {
			formData.put("BusiCode", params.getBusiCode());
		} else {
			formData.put("type", params.getType());
		}

		return GsonUtils.toJson(formData);
	}
}
