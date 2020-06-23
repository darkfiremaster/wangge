package com.shinemo.wangge.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 16:05
 * @Desc
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "daosanjiao")
@EnableConfigurationProperties(DaosanjiaoPropertity.class)
public class DaosanjiaoPropertity {

    private String seed;
    private String domain;
    private String todoDetailUrl;

}
