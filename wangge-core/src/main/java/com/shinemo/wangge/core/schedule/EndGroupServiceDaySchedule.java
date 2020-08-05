package com.shinemo.wangge.core.schedule;

import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.stallup.common.statemachine.InvalidStateTransitionException;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.event.StallUpEvent;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;
import com.shinemo.stallup.domain.request.StallUpEndRequest;
import com.shinemo.wangge.core.config.StallUpStateMachine;
import com.shinemo.wangge.dal.mapper.GroupServiceDayMapper;
import com.shinemo.wangge.dal.mapper.StallUpActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时关闭集团服务日
 *
 * @date 2020-08-04
 */
@Slf4j
@Component
public class EndGroupServiceDaySchedule {

	@Resource
	private GroupServiceDayMapper groupServiceDayMapper;

	@Scheduled(cron = "0 0 0 * * ? ")
	//@Scheduled(cron = "0 */1 * * * ?")
	public void execute() {
		long begin = System.currentTimeMillis();
		log.info("[EndStallUpSchedule] execute start");

		GroupServiceDayQuery groupServiceDayQuery = new GroupServiceDayQuery();
		List<Integer> statusList = new ArrayList<>();
		statusList.add(GroupServiceDayStatusEnum.NOT_START.getId());
		statusList.add(GroupServiceDayStatusEnum.PROCESSING.getId());
		groupServiceDayQuery.setStatusList(statusList);
		List<GroupServiceDayDO> groupServiceDayDOS = groupServiceDayMapper.find(groupServiceDayQuery);
		for (GroupServiceDayDO serviceDayDO: groupServiceDayDOS) {
			if (serviceDayDO.getPlanEntTime().getTime() < begin) {
				//自动结束

			}
		}

		log.info("[EndStallUpSchedule] execute end, costTime:{}ms", System.currentTimeMillis() - begin);
	}
}
