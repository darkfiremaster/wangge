package com.shinemo.wangge.core.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
import com.shinemo.my.redis.domain.RedisSentinelNode;
import com.shinemo.rds.RedisConf;
import com.shinemo.rds.RedisConfProxy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

@Configuration
@EnableMethodCache(basePackages = "com.shinemo.wangge.core.service")
@EnableCreateCacheAnnotation
public class JetCacheConfig {

    @Bean
    public Pool<Jedis> pool(@Value("${shinemo.redis.dataName}") String dataName,
            @Value("${shinemo.redis.database}") Integer database) {
        GenericObjectPoolConfig pc = new GenericObjectPoolConfig();
        pc.setMinIdle(5);
        pc.setMaxIdle(20);
        pc.setMaxTotal(50);
        RedisConf redisConf = RedisConfProxy.get(dataName);
        RedisSentinelNode node = new RedisSentinelNode(redisConf.getSentinels(), redisConf.getMastername());
        node.setDatabase(database);
        node.setPassword(redisConf.getPasswd());
        return new JedisSentinelPool(node.getMasterName(), node.getNodeList(), pc, 3000, node.getPassword(), database);
    }

    @Bean
    public ConfigProvider configProvider() {
        return new ConfigProvider();
    }

    @Bean
    public GlobalCacheConfig config(Pool<Jedis> pool) {
        Map localBuilders = new HashMap();
        EmbeddedCacheBuilder localBuilder = CaffeineCacheBuilder.createCaffeineCacheBuilder().limit(100)
                .keyConvertor(FastjsonKeyConvertor.INSTANCE);

        localBuilders.put(CacheConsts.DEFAULT_AREA, localBuilder);

        Map remoteBuilders = new HashMap();
        RedisCacheBuilder remoteCacheBuilder = RedisCacheBuilder.createRedisCacheBuilder()
                .keyConvertor(FastjsonKeyConvertor.INSTANCE).valueEncoder(JavaValueEncoder.INSTANCE)
                .valueDecoder(JavaValueDecoder.INSTANCE).jedisPool(pool);
        remoteBuilders.put(CacheConsts.DEFAULT_AREA, remoteCacheBuilder);

        GlobalCacheConfig globalCacheConfig = new GlobalCacheConfig();
        globalCacheConfig.setLocalCacheBuilders(localBuilders);
        globalCacheConfig.setRemoteCacheBuilders(remoteBuilders);
        globalCacheConfig.setStatIntervalMinutes(15);
        globalCacheConfig.setAreaInCacheName(false);
        return globalCacheConfig;
    }
}