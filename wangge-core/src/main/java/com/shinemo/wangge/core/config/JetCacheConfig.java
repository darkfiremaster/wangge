package com.shinemo.wangge.core.config;

import com.alicp.jetcache.anno.CacheConsts;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.alicp.jetcache.anno.support.ConfigProvider;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import com.alicp.jetcache.embedded.CaffeineCacheBuilder;
import com.alicp.jetcache.embedded.EmbeddedCacheBuilder;
import com.alicp.jetcache.redis.RedisCacheBuilder;
import com.alicp.jetcache.support.FastjsonKeyConvertor;
import com.alicp.jetcache.support.JavaValueDecoder;
import com.alicp.jetcache.support.JavaValueEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableMethodCache(basePackages = "com.shinemo.wangge.core.service")
@EnableCreateCacheAnnotation
public class JetCacheConfig {

    @Value("${shinemo.redis.sentinel.master}")
    private String masterName;
    @Value("${shinemo.redis.sentinel.nodes}")
    private String ipaddrs;
    @Value("${shinemo.redis.database}")
    private int database;
    @Value("${shinemo.redis.password}")
    private String password;

    @Bean
    public Pool<Jedis> pool() {
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(5);
        pc.setMaxIdle(20);
        pc.setMaxTotal(50);
        String[] ipArray = ipaddrs.split(",");
        Set<String> sentinels = new HashSet<>();
        for (String str : ipArray) {
            if (StringUtils.isNotBlank(str)) {
                sentinels.add(str);
            }
        }
        return new JedisSentinelPool(masterName,sentinels,pc,3000,password,database);
    }



    @Bean
    public ConfigProvider configProvider() {
        return new ConfigProvider();
    }

    @Bean
    public GlobalCacheConfig config(Pool<Jedis> pool) {
        Map localBuilders = new HashMap();
        EmbeddedCacheBuilder localBuilder = CaffeineCacheBuilder
                .createCaffeineCacheBuilder()
                .limit(100)
                .keyConvertor(FastjsonKeyConvertor.INSTANCE);

        localBuilders.put(CacheConsts.DEFAULT_AREA, localBuilder);

        Map remoteBuilders = new HashMap();
        RedisCacheBuilder remoteCacheBuilder = RedisCacheBuilder.createRedisCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE)
                .valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE)
                .jedisPool(pool);
        remoteBuilders.put(CacheConsts.DEFAULT_AREA, remoteCacheBuilder);

        GlobalCacheConfig globalCacheConfig = new GlobalCacheConfig();
        globalCacheConfig.setLocalCacheBuilders(localBuilders);
        globalCacheConfig.setRemoteCacheBuilders(remoteBuilders);
        globalCacheConfig.setStatIntervalMinutes(15);
        globalCacheConfig.setAreaInCacheName(false);
        return globalCacheConfig;
    }
}