package com.shinemo.wangge.core.delay;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DelayJobTimer {

    public static final String delayJobsTag = "smartgrid_todo_delay_job_queue";

    @Resource
    private RedissonClient client;

    @Resource
    private ApplicationContext context;

    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @PostConstruct
    public void startJobTimer() {
        RBlockingQueue<DelayJob> blockingQueue = client.getBlockingQueue(delayJobsTag, JsonJacksonCodec.INSTANCE);

        //下面两行 必不可少 防止出现 服务器重启后，延迟队列take数据阻塞，不执行，必须等到下一个内容offer时，队列才会把阻塞的消息全部处理掉
        RDelayedQueue<DelayJob> delayedQueue = client.getDelayedQueue(blockingQueue);
        delayedQueue.offer(null, 1, TimeUnit.SECONDS);

        new Thread(() -> {
            while (true) {
                try {
                    DelayJob job = blockingQueue.take();
                    log.info("[startJobTimer] 获取到延迟任务:{}", job);
                    log.info("[startJobTimer] 延迟队列剩余数量:{}", blockingQueue.size());
                    if (job == null) {
                        continue;
                    }
                    executorService.execute(new ExecutorTask(context, job));
                } catch (Exception e) {
                    log.error("[startJobTimer] 出现异常:{}", e.getMessage());
                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    class ExecutorTask implements Runnable {

        private ApplicationContext context;

        private DelayJob delayJob;

        public ExecutorTask(ApplicationContext context, DelayJob delayJob) {
            this.context = context;
            this.delayJob = delayJob;
        }

        @Override
        public void run() {
            ExecuteJob service = (ExecuteJob) context.getBean(delayJob.getClazz());
            service.execute(delayJob);
        }
    }
}