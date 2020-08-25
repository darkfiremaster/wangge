package com.shinemo.wangge.core.delay;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DelayJobService {

    @Resource
    private RedissonClient client;

    @Resource
    private ApplicationContext context;

    @Resource
    private ThreadPoolTaskExecutor delayJobServiceExecutor;

    /**
     * 提交延迟任务
     *
     * @param job
     * @param delay    延迟的时间
     * @param timeUnit
     */
    public void submitJob(DelayJob job, Long delay, TimeUnit timeUnit) {
        Assert.notNull(job, "任务不能为空");
        Assert.notNull(delay, "延迟时间不能为空");
        Assert.notNull(timeUnit, "时间单位不能为空");
        Assert.notNull(job.getJobParams(), "任务参数不能为空");
        Assert.notNull(job.getClazz(), "任务实现类不能为空");

        if (delay <= 0) {
            log.error("[submitJob] 延迟时间不能小于0, 任务:{},延迟时间:{}", job, delay);
            return;
        }

        RBlockingQueue<Object> blockingQueue = client.getBlockingQueue(DelayJobTimer.delayJobsTag, JsonJacksonCodec.INSTANCE);
        RDelayedQueue delayedQueue = client.getDelayedQueue(blockingQueue);
        delayedQueue.offer(job, delay, timeUnit);
        log.info("[submitJob] 添加延迟任务成功,job:{}", job);
    }

    /**
     * 直接执行任务
     *
     * @param job
     */
    public void executeJobNow(DelayJob job) {
        Assert.notNull(job, "任务不能为空");
        Assert.notNull(job.getJobParams(), "任务参数不能为空");
        Assert.notNull(job.getClazz(), "任务实现类不能为空");

        delayJobServiceExecutor.execute(new ExecutorDelayTask(context, job));
        log.info("[executeJobNow] 直接执行延迟任务,job:{}", job);

    }
}