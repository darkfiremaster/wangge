package com.shinemo.wangge.core.event;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.wangge.core.delay.DaoSanJiaoWarnDelayJobExecutor;
import com.shinemo.wangge.core.delay.DelayJob;
import com.shinemo.wangge.core.delay.DelayJobService;
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
public class DaoSanJiaoTodoCreateEventListener {

    @Resource
    private DelayJobService delayJobService;

    @EventListener(classes = {DaoSanJiaoTodoCreateEvent.class})
    @Async
    public void handleDaoSanJiaoTodoCreateEvent(DaoSanJiaoTodoCreateEvent event) {
        log.info("[handleDaoSanJiaoTodoCreateEvent] 监听到事件:{}", event);
        TodoDO todoDO = event.getTodoDO();
        //增加延迟任务
        //提前4小时提醒,计算执行时间,判断执行时间是否在休息时间内,在的话就不投递
        DateTime executeTime = DateUtil.offsetHour(todoDO.getOperatorTime(), -4);//向前偏移4小时

        //如果执行时间小于当前时间,则不执行
        long delayTime = executeTime.getTime() - System.currentTimeMillis();
        if (delayTime <= 0) {
            return;
        }

        //休息时间不做提醒:中午12:00-14:30；18:00-次日8:30
        String today = DateUtil.today();
        String beginTime1 = today + " 12:00:00";
        String endTime1 = today + " 14:30:00";
        DateTime begin1 = DateUtil.parseDateTime(beginTime1);
        DateTime end1 = DateUtil.parseDateTime(endTime1);
        boolean isIn = DateUtil.isIn(executeTime, begin1, end1);

        DateTime tomorrow = DateUtil.tomorrow();
        String tomorrowTime = DateUtil.formatDate(tomorrow);
        String beginTime2 = today + " 18:00:00";
        String endTime2 = tomorrowTime + " 08:00:00";
        DateTime begin2 = DateUtil.parseDateTime(beginTime2);
        DateTime end2 = DateUtil.parseDateTime(endTime2);
        boolean isIn2 = DateUtil.isIn(executeTime, begin2, end2);

        if (isIn || isIn2) {
            //在休息时间内
            return;
        } else {
            DelayJob delayJob = new DelayJob();
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", todoDO.getId());
            delayJob.setJobParams(map);
            delayJob.setExecuteTime(DateUtil.formatDateTime(executeTime));
            delayJob.setClazz(DaoSanJiaoWarnDelayJobExecutor.class);
            delayJobService.submitJob(delayJob, delayTime, TimeUnit.MILLISECONDS);
        }
    }
}
