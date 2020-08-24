package com.shinemo.wangge.core.delay;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.shinemo.common.tools.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author shangkaihui
 * @Date 2020/8/24 15:43
 * @Desc
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestDelayJobController {

    @Resource
    private DelayJobService delayJobService;

    @Resource
    private RedissonClient redissonClient;

    @GetMapping("/stallUpDelay")
    public ApiResult putJob(String id,String startTime) {
        DelayJob delayJob = new DelayJob();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("startTime", startTime);
        delayJob.setJobParams(map);
        delayJob.setClazz(StallUpStartDelayJobExecute.class);
        delayJob.setExecuteTime(startTime);

        long delayTime = DateUtil.parseDateTime(startTime).getTime() - System.currentTimeMillis();
        delayJobService.submitJob(delayJob, delayTime, TimeUnit.MILLISECONDS);
        log.info("[putJob]添加任务成功,job:{}", delayJob);

        return ApiResult.of(0);
    }

    @GetMapping("/getQueueSize")
    public ApiResult getQueueSize() {
        RBlockingQueue<DelayJob> blockingQueue1 = redissonClient.getBlockingQueue(DelayJobTimer.delayJobsTag);
        RDelayedQueue<DelayJob> delayedQueue = redissonClient.getDelayedQueue(blockingQueue1);
        List<DelayJob> delayJobs = delayedQueue.readAll();
        delayedQueue.clear();
        System.out.println("delayJobs = " + delayJobs);
        log.info("队列大小:{}", delayedQueue.size());
        return ApiResult.of(0);
    }

    @GetMapping("/put")
    public ApiResult put() {
        RBlockingQueue<Integer> blockingQueue1 = redissonClient.getBlockingQueue("test_queue",JsonJacksonCodec.INSTANCE);
        blockingQueue1.offer(RandomUtil.randomInt());
        log.info("队列大小:{}", blockingQueue1.size());
        return ApiResult.of(0);
    }

    @GetMapping("/get")
    public ApiResult get() throws InterruptedException {
        RBlockingQueue<Integer> blockingQueue1 = redissonClient.getBlockingQueue("test_queue",JsonJacksonCodec.INSTANCE);
        Integer result = blockingQueue1.take();
        log.info("队列大小:{}", blockingQueue1.size());
        log.info("result:{}", result);
        return ApiResult.of(0);
    }
}
