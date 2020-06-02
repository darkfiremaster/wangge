package com.shinemo.wangge.web.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.shinemo.wangge.core.config.SpringContextHolder;
import com.shinemo.wangge.web.intercepter.IntranetInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Beans
 *
 * @author Zhihao Luo
 * @date 2019-07-20
 */
@Configuration
public class SmartGridBeansConfig {


	@Bean
	public IntranetInterceptor intranetInterceptor() {
		return new IntranetInterceptor();
	}


	@Bean
	public Jackson2ObjectMapperBuilder objectMapperBuilder(){
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.serializationInclusion(JsonInclude.Include.NON_NULL);
		return builder;
	}

}
