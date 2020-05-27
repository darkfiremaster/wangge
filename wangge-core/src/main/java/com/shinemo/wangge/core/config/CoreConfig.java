package com.shinemo.wangge.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.shinemo.client.async.InternalEventBus;
import com.shinemo.jce.common.config.CenterConfig;
import com.shinemo.jce.common.config.ProviderConfig;
import com.shinemo.my.redis.domain.RedisSentinelNode;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.my.redis.service.impl.RedisServiceImpl;

/**
 * @author htdong
 * @date 2020年5月27日 上午10:22:56
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class CoreConfig {

    @Bean(initMethod = "init")
    public CenterConfig centerConfig(@Value("${aace.center.ipport}") String ipAndPort,
            @Value("${appname}") String appName, @Value("${aace.trace}") boolean enableTrace,
            @Value("${aace.sample}") Integer sample) {
        CenterConfig centerConfig = new CenterConfig();
        centerConfig.setIpAndPort(ipAndPort);
        centerConfig.setAppName(appName);
        centerConfig.setEnableTrace(enableTrace);
        centerConfig.setSample(sample);
        return centerConfig;
    }

    @Bean(initMethod = "init")
    public ProviderConfig providerConfig(@Value("${wangge.aace.port}") Integer port,
            @Value("${wangge.aace.proxy.name}") String proxy) {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setPort(port);
        providerConfig.setProxy(proxy);
        return providerConfig;
    }

    @Bean
    public InternalEventBus internalEventBus() {
        InternalEventBus eventBus = new InternalEventBus();
        return eventBus;
    }

    @Bean(name = "redisService")
    public RedisService redisService(@Value("${shinemo.redis.database}") Integer database,
            @Value("${shinemo.redis.sentinel.nodes}") String redisIp,
            @Value("${shinemo.redis.sentinel.master}") String masterName,
            @Value("${shinemo.redis.password}") String pwd) {
        RedisSentinelNode node = new RedisSentinelNode(redisIp, masterName);
        node.setDatabase(database);
        node.setPassword(pwd);
        return new RedisServiceImpl(node);
    }
}