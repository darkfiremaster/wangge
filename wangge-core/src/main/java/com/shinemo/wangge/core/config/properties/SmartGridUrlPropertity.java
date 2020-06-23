package com.shinemo.wangge.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shangkaihui
 * @Date 2020/6/23 10:44
 * @Desc
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "smartgrid")
@EnableConfigurationProperties(SmartGridUrlPropertity.class)
public class SmartGridUrlPropertity {

    private String indexUrl;

    private String createStallupUrl;
}
