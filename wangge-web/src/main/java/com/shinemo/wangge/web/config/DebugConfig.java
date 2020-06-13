package com.shinemo.wangge.web.config;

import com.shinemo.wangge.web.intercepter.DebugInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * daily拦截器
 *
 * @author Chenzhe Mao
 * @date 2020-04-13
 */
@Configuration
@ConditionalOnProperty(prefix = "sm", name = "conf.ENV", havingValue = "daily")
public class DebugConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new DebugInterceptor())
			.addPathPatterns("/stallUp/**")
			.addPathPatterns("/smartGrid/**")
			.addPathPatterns("/sweepFloor/**")
			.addPathPatterns("/thirdapi/**")
			.addPathPatterns("/operate/**")
			.addPathPatterns("/todo/thing/**")
				.excludePathPatterns("/todo/operateTodoThing")
			.order(Ordered.HIGHEST_PRECEDENCE);
	}
}
