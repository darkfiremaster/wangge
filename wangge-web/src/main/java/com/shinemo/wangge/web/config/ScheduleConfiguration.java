package com.shinemo.wangge.web.config;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * 定时任务配置
 *
 * @author Chenzhe Mao
 * @date 2020-04-26
 */
@Component
public class ScheduleConfiguration implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
	}
}
