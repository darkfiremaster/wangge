package com.shinemo.wangge.web.controller.common;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.wangge.core.delay.DelayJob;
import com.shinemo.wangge.core.delay.DelayJobService;
import com.shinemo.wangge.core.delay.DelayJobTimer;
import com.shinemo.wangge.core.delay.executor.StallUpStartDelayJobExecutor;
import com.shinemo.wangge.core.schedule.YuJingWarnSchedule;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author shangkaihui
 * @Date 2020/8/24 15:43
 * @Desc 用于测试
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    private DelayJobService delayJobService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private YuJingWarnSchedule yuJingWarnSchedule;

    @GetMapping("/stallUpDelay")
    public ApiResult putJob(String id, String startTime) {

        if (StrUtil.isBlank(startTime)) {
            DateTime dateTime = DateUtil.offsetSecond(DateUtil.parseDateTime(DateUtil.now()), 5);
            startTime = DateUtil.formatDateTime(dateTime);
        }

        DelayJob delayJob = new DelayJob();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("startTime", startTime);
        delayJob.setJobParams(map);
        delayJob.setClazz(StallUpStartDelayJobExecutor.class);
        delayJob.setExecuteTime(startTime);

        long delayTime = DateUtil.parseDateTime(startTime).getTime() - System.currentTimeMillis();
        delayJobService.submitJob(delayJob, delayTime, TimeUnit.MILLISECONDS);

        return ApiResult.of(0);
    }

    @GetMapping("/getQueueSize")
    public ApiResult getQueueSize() {
        RBlockingQueue<DelayJob> blockingQueue = redissonClient.getBlockingQueue(DelayJobTimer.delayJobsTag);
        RDelayedQueue<DelayJob> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        log.info("block队列大小:{}", blockingQueue.size());
        log.info("delay队列大小:{}", delayedQueue.size());

        //blockingQueue.stream().forEach((q) -> System.out.println(q));

        System.out.println("----");

        delayedQueue.stream().forEach((q) -> System.out.println(q));

        return ApiResult.of(0);
    }

    @GetMapping("/yuJingWarnSchedule")
    public ApiResult yuJingWarnSchedule() {
        yuJingWarnSchedule.yujingTodoTimeoutWarn();
        return ApiResult.of(0);
    }

}
