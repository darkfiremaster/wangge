package com.shinemo.wangge.core.delay;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DelayJobTimer {

    public static final String delayJobsTag = "smartgrid_delay_job_queue";

    @Resource
    private RedissonClient client;

    @Resource
    private ApplicationContext context;

    @Resource
    private ThreadPoolTaskExecutor delayJobServiceExecutor;

    @PostConstruct
    public void startDelayJobTimer() {
        RBlockingQueue<DelayJob> blockingQueue = client.getBlockingQueue(delayJobsTag, JsonJacksonCodec.INSTANCE);
        //下面一行 必不可少 防止出现 服务器重启后，延迟队列take数据阻塞，不执行，必须等到下一个内容offer时，队列才会把阻塞的消息全部处理掉
        RDelayedQueue<DelayJob> delayedQueue = client.getDelayedQueue(blockingQueue);
        delayedQueue.offer(null, 1, TimeUnit.SECONDS);

        new Thread(() -> {
            while (true) {
                try {
                    DelayJob job = blockingQueue.take();
                    log.info("[startDelayJobTimer] 获取到延迟任务:{}, 剩余任务数:{}", job, delayedQueue.size());
                    if (job == null) {
                        continue;
                    }
                    delayJobServiceExecutor.execute(new ExecutorDelayTask(context, job));
                } catch (Exception e) {
                    log.error("[startDelayJobTimer] 执行延迟任务出现异常,异常原因:{}", e.getMessage(), e);
                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, "delayJobTimer").start();

        log.info("init startDelayJobTimer success");
    }
}