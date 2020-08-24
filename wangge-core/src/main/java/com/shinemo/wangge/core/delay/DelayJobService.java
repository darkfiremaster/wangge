package com.shinemo.wangge.core.delay;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class DelayJobService {

    @Resource
    private RedissonClient client;

    /**
     * 提交延迟任务
     * @param job
     * @param delay 延迟的时间
     * @param timeUnit
     */
    public void submitJob(DelayJob job, Long delay, TimeUnit timeUnit) {
        RBlockingQueue<Object> blockingQueue = client.getBlockingQueue(DelayJobTimer.delayJobsTag, JsonJacksonCodec.INSTANCE);
        RDelayedQueue delayedQueue = client.getDelayedQueue(blockingQueue);
        delayedQueue.offer(job, delay, timeUnit);
    }

}