package com.shinemo.wangge.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 16:05
 * @Desc 预警工单
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "zhuangwei")
@EnableConfigurationProperties(ZhuangweiPropertity.class)
public class ZhuangweiPropertity {

    private String seed;
    private String domain;
    //跳转装移数据看板url
    private String dataBoardUrl;

}
