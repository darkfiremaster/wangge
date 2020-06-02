package com.shinemo.wangge.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author htdong
 * @date 2020年4月15日 上午11:30:57
 */
@ImportResource({
        "classpath:wangge-web.xml"
})
@ComponentScan("com.shinemo.wangge")
@SpringBootApplication
@EnableNacosConfig(globalProperties = @NacosProperties(enableRemoteSyncConfig = "true", username = "${nacos.username}", password = "${nacos.password}"))
@NacosPropertySource(dataId = "wangge", groupId = "${nacos.group.id}")
// @NacosPropertySource(dataId = "wangge", groupId = "dynamic", autoRefreshed =
// true, first = true)
@MapperScan(basePackages = {
        "com.shinemo.wangge.mapper"
})
@EnableScheduling
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}