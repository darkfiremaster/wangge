package com.shinemo.wangge.core.event;

import cn.hutool.core.date.DateUtil;
import com.shinemo.wangge.core.delay.DelayJob;
import com.shinemo.wangge.core.delay.DelayJobService;
import com.shinemo.wangge.core.delay.StallUpEndDelayJobExecutor;
import com.shinemo.wangge.core.delay.StallUpStartDelayJobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author shangkaihui
 * @Date 2020/8/25 11:04
 * @Desc
 */
@Component
@Slf4j
public class StallUpCreateEventListener {

    @Resource
    private DelayJobService delayJobService;

    @EventListener(classes = {StallUpCreateEvent.class})
    @Async
    public void handleStallUpCreateEvent(StallUpCreateEvent event) {
        log.info("[handleStallUpCreateEvent] 监听到事件:{},子活动id:{}", event, event.getStallUpActivity().getId());

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", event.getStallUpActivity().getId());

        //添加延迟任务
        DelayJob stallUpStartDelayJob = new DelayJob();
        stallUpStartDelayJob.setJobParams(map);
        stallUpStartDelayJob.setExecuteTime(DateUtil.formatDateTime(event.getStallUpActivity().getStartTime()));
        stallUpStartDelayJob.setClazz(StallUpStartDelayJobExecutor.class);
        long stallUpStartDelayJobDelay= event.getStallUpActivity().getStartTime().getTime() - System.currentTimeMillis();
        delayJobService.submitJob(stallUpStartDelayJob, stallUpStartDelayJobDelay, TimeUnit.MILLISECONDS);

        DelayJob stallUpEndDelayJob = new DelayJob();
        stallUpEndDelayJob.setJobParams(map);
        stallUpEndDelayJob.setExecuteTime(DateUtil.formatDateTime(event.getStallUpActivity().getEndTime()));
        stallUpEndDelayJob.setClazz(StallUpEndDelayJobExecutor.class);
        long stallUpEndDelayJobDelay = event.getStallUpActivity().getEndTime().getTime() - System.currentTimeMillis();
        delayJobService.submitJob(stallUpEndDelayJob, stallUpEndDelayJobDelay, TimeUnit.MILLISECONDS);

    }
}
