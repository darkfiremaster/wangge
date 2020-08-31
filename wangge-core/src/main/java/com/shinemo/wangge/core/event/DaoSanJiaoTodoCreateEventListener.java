package com.shinemo.wangge.core.event;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.wangge.core.delay.DelayJob;
import com.shinemo.wangge.core.delay.DelayJobService;
import com.shinemo.wangge.core.delay.executor.DaoSanJiaoWarnDelayJobExecutor;
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
        //00:00-08:30 12:00-14:30 18:00-23:59 在这三个时间段内,不做提醒
        //只有12点半-16点 18:30:00-22:00之间的可以被通知
        DateTime time = DateUtil.parseTime(executeTime.toTimeStr());
        DateTime begin = DateUtil.parseTime("00:00:00");
        DateTime end = DateUtil.parseTime("08:30:00");
        boolean isInMorning = DateUtil.isIn(time, begin, end);

        DateTime begin1 = DateUtil.parseTime("12:00:00");
        DateTime end1 = DateUtil.parseTime("14:30:00");
        boolean isInNoon = DateUtil.isIn(time, begin1, end1);

        DateTime begin2 = DateUtil.parseTime("18:00:00");
        DateTime end2 = DateUtil.parseTime("23:59:59");
        boolean isInNight = DateUtil.isIn(time, begin2, end2);

        if (isInMorning || isInNoon || isInNight) {
            //在休息时间内
            log.info("[handleDaoSanJiaoTodoCreateEvent] 倒三角工单超时提醒时间在休息时间内,工单ID:{}, 工单执行时间:{},isInMorning:{},isInNoon:{},isInNight:{}",
                    todoDO.getThirdId(), todoDO.getOperatorTime(), isInMorning, isInNoon, isInNight);
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

    public static void main(String[] args) {

        DateTime dateTime = DateUtil.parseDateTime("2020-09-03 02:00:00");
        String time = dateTime.toTimeStr();
        System.out.println("time = " + time);
        DateTime executeTime = DateUtil.offsetHour(dateTime, -4);//向前偏移4小时
        System.out.println("executeTime = " + executeTime);
        //如果执行时间小于当前时间,则不执行
        long delayTime = executeTime.getTime() - System.currentTimeMillis();
        if (delayTime <= 0) {
            return;
        }

        //休息时间不做提醒:中午12:00-14:30；18:00-次日8:30
        //00:00-08:30 12:00-14:30 18:00-23:59 在这三个时间段内,不做提醒
        executeTime = DateUtil.parseTime(executeTime.toTimeStr());
        System.out.println("executeTime = " + executeTime);
        DateTime begin = DateUtil.parseTime("00:00:00");
        DateTime end = DateUtil.parseTime("08:00:00");
        boolean isInMorning = DateUtil.isIn(executeTime, begin, end);
        System.out.println("isInMorning = " + isInMorning);
        DateTime begin1 = DateUtil.parseTime("12:00:00");
        DateTime end1 = DateUtil.parseTime("14:30:00");
        boolean isInNoon = DateUtil.isIn(executeTime, begin1, end1);
        System.out.println("isInNoon = " + isInNoon);
        DateTime begin2 = DateUtil.parseTime("18:00:00");
        DateTime end2 = DateUtil.parseTime("23:59:59");
        boolean isInNight = DateUtil.isIn(executeTime, begin2, end2);
        System.out.println("isInNight = " + isInNight);
    }
}
