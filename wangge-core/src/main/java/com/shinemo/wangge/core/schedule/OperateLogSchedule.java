package com.shinemo.wangge.core.schedule;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.wangge.core.service.operate.OperateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 19:10
 * @Desc
 */
@Component
@Slf4j
public class OperateLogSchedule implements SchedulingConfigurer {

    @Autowired
    private OperateService operateService;


    @NacosValue(value = "${wangge.operatelog.sync.cron}", autoRefreshed = true)
    private String logSyncCron = "0 0/30 0/1 * * ?";

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        Runnable runnable = () -> {
            long begin = System.currentTimeMillis();
            log.info("[configureTasks] schedule start sync operate log");
            operateService.syncLogFromRedisToDB();
            log.info("[configureTasks] schedule end sync operate log,耗时:{}", System.currentTimeMillis() - begin);
        };

        Trigger trigger = triggerContext -> {
            //任务触发，可修改任务的执行周期.
            CronTrigger trigger1 = new CronTrigger(logSyncCron);
            Date nextExec = trigger1.nextExecutionTime(triggerContext);
            return nextExec;
        };
        scheduledTaskRegistrar.addTriggerTask(runnable, trigger);

    }
}
