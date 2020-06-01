package com.shinemo.wangge.web.config;

import com.shinemo.my.redis.domain.RedisSentinelNode;
import com.shinemo.my.redis.service.RedisLock;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.my.redis.service.impl.RedisLockImpl;
import com.shinemo.my.redis.service.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(name = "redisService")
    @ConditionalOnMissingBean(RedisService.class)
    @ConditionalOnProperty(prefix = "shinemo.redis", name = {"sentinel.nodes", "sentinel.master", "password", "database"})
    public RedisService redisServiceUrl(@Value("${shinemo.redis.sentinel.nodes}") String sentinelNodes,
                                        @Value("${shinemo.redis.sentinel.master}") String sentinelMaster,
                                        @Value("${shinemo.redis.password}") String password,
                                        @Value("${shinemo.redis.database}") Integer database) {
        String nodes = StringUtils.arrayToDelimitedString(StringUtils.commaDelimitedListToStringArray(sentinelNodes), ";");
        RedisSentinelNode redisNode = new RedisSentinelNode(nodes, sentinelMaster);
        redisNode.setDatabase(database);
        redisNode.setPassword(password);
        return new RedisServiceImpl(redisNode);
    }


    @Bean(name = "redisLock")
    @DependsOn("redisService")
    public RedisLock redisLock(@Qualifier("redisService") RedisService redisService) {
        RedisLockImpl redisLock = new RedisLockImpl();
        redisLock.setRedisService(redisService);
        return redisLock;
    }


}