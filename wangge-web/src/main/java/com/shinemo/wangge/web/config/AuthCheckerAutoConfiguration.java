package com.shinemo.wangge.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(SmCommonProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "sm", name = "auth.enabled", matchIfMissing = true, havingValue = "true")
public class AuthCheckerAutoConfiguration{
    private SmCommonProperties properties;

    public AuthCheckerAutoConfiguration(SmCommonProperties properties, ApplicationContext context) {
        this.properties = properties;
    }

    public SmCommonProperties getProperties() {
        return properties;
    }

    public void setProperties(SmCommonProperties properties) {
        this.properties = properties;
    }
}
