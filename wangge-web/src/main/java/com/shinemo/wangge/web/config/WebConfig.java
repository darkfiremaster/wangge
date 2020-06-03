package com.shinemo.wangge.web.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shinemo.client.filter.LoggerFilter;

/**
 * @author htdong
 * @date 2020年6月3日 下午3:42:09
 */
@Configuration
public class WebConfig {
    
    @Bean
    public LoggerFilter loggerFilter() {
        return new LoggerFilter();
    }

    @Bean
    public FilterRegistrationBean<LoggerFilter> logFilterRegiste() {
        FilterRegistrationBean<LoggerFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(loggerFilter());
        Set<String> set = new HashSet<>();
        set.add("/*");
        filterRegistrationBean.setUrlPatterns(set);
        return filterRegistrationBean;
    }
}