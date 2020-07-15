package com.shinemo.wangge.core.schedule;

import com.shinemo.stallup.common.statemachine.InvalidStateTransitionException;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.event.StallUpEvent;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;
import com.shinemo.stallup.domain.request.StallUpEndRequest;
import com.shinemo.wangge.core.config.StallUpStateMachine;
import com.shinemo.wangge.dal.mapper.StallUpActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时关闭摆摊任务
 *
 * @author Chenzhe Mao
 * @date 2020-04-12
 */
@Slf4j
@Component
public class EndStallUpSchedule {

	@Resource
	private StallUpActivityMapper stallUpActivityMapper;

	//@Scheduled(cron = "0 0 0 * * ? ")
	@Scheduled(cron = "0 */1 * * * ?")
	public void execute() {
		long begin = System.currentTimeMillis();
		log.info("[EndStallUpSchedule] execute start");
		Long now = begin;
		StallUpActivityQuery query = new StallUpActivityQuery();
		List<Integer> statusList = new ArrayList<>();
		statusList.add(StallUpStatusEnum.STARTED.getId());
		statusList.add(StallUpStatusEnum.PREPARE.getId());
		query.setStatusList(statusList);
		List<StallUpActivity> list = stallUpActivityMapper.find(query);
		for (StallUpActivity stallUpActivity : list) {
			if (stallUpActivity.getEndTime().getTime() < now) {
				StallUpEndRequest request = new StallUpEndRequest();
				request.setId(stallUpActivity.getId());
				request.setMobile(stallUpActivity.getMobile());
				request.setStatus(stallUpActivity.getStatus());
				request.setActivity(stallUpActivity);
				try {
					StallUpStateMachine.handler(request, new StallUpEvent(StallUpEvent.StallUpEventEnum.AUTO_END));
				} catch (InvalidStateTransitionException e) {
					log.error("[EndStallUpSchedule] execute", e);
				}
			}
		}
		log.info("[EndStallUpSchedule] execute end, costTime:{}ms", System.currentTimeMillis() - begin);
	}
}
