package com.shinemo.wangge.test.web;

import com.shinemo.wangge.web.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBucket;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author shangkaihui
 * @Date 2020/8/24 14:57
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@Slf4j
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;


    @Test
    public void put() {
        RBucket<String> bucket = redissonClient.getBucket("test_bucket");
        bucket.set("test_redisson", 10, TimeUnit.MINUTES);
        String result = bucket.get();
        System.out.println("result = " + result);
    }

    @Test
    public void send() {
        RBlockingQueue<String> testQueue = redissonClient.getBlockingQueue("test_delay_queue");
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(testQueue);
        delayedQueue.offer("1", 1, TimeUnit.MINUTES);
        log.info("投递成功");
        delayedQueue.destroy();
    }


    @Test
    public void get() throws InterruptedException {
        RBlockingQueue<String> testQueue = redissonClient.getBlockingQueue("test_delay_queue");
        String take = testQueue.take();
        System.out.println("take = " + take);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
