package com.shinemo.wangge.web.config;

import com.shinemo.my.redis.domain.RedisSentinelNode;
import com.shinemo.my.redis.service.RedisLock;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.my.redis.service.impl.RedisLockImpl;
import com.shinemo.my.redis.service.impl.RedisServiceImpl;
import com.shinemo.rds.RedisConf;
import com.shinemo.rds.RedisConfProxy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(name = "redisService")
    @ConditionalOnMissingBean(RedisService.class)
    public RedisService redisServiceUrl(@Value("${shinemo.redis.dataName}") String dataName,
                                        @Value("${shinemo.redis.database}") Integer database) {
        RedisConf redisConf = RedisConfProxy.get(dataName);
        RedisSentinelNode node = new RedisSentinelNode(redisConf.getSentinels(), redisConf.getMastername());
        node.setDatabase(database);
        node.setPassword(redisConf.getPasswd());
        return new RedisServiceImpl(node);
    }

    @Bean(name = "redisLock")
    @DependsOn("redisService")
    public RedisLock redisLock(@Qualifier("redisService") RedisService redisService) {
        RedisLockImpl redisLock = new RedisLockImpl();
        redisLock.setRedisService(redisService);
        return redisLock;
    }

    /**
     * 哨兵模式 redisson 客户端
     *
     * @return
     */

    @Bean(name = "redissonClient")
    @ConditionalOnMissingBean(name = "redissonClient")
    public RedissonClient redissonSentinel(@Value("${shinemo.redis.dataName}") String dataName,
                                    @Value("${shinemo.redis.database}") Integer database) {
        RedisConf redisConf = RedisConfProxy.get(dataName);
        Config config = new Config();
        String[] nodes = redisConf.getSentinels().split(",");
        List<String> newNodes = new ArrayList(nodes.length);
        Arrays.stream(nodes).forEach((index) -> newNodes.add(
                index.startsWith("redis://") ? index : "redis://" + index));

        config.useSentinelServers()
                .addSentinelAddress(newNodes.toArray(new String[0]))
                .setMasterName(redisConf.getMastername())
                .setPassword(redisConf.getPasswd())
                .setDatabase(database);


        return Redisson.create(config);
    }

}