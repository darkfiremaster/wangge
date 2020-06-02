package com.shinemo.wangge.core.schedule;

import com.shinemo.wangge.core.config.StallUpConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时刷新配置任务
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@Slf4j
@Component
public class FlushConfigSchedule {

	@Resource
	private StallUpConfig stallUpConfig;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void execute(){
		long begin = System.currentTimeMillis();
		log.info("[FlushConfigSchedule] execute start");
		stallUpConfig.init();
		log.info("[FlushConfigSchedule] execute end, costTime:{}ms", System.currentTimeMillis() - begin);
	}
}
