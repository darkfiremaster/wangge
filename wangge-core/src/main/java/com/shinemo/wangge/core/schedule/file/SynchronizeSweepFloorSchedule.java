package com.shinemo.wangge.core.schedule.file;

import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.stallup.common.statemachine.InvalidStateTransitionException;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.event.StallUpEvent;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;
import com.shinemo.stallup.domain.request.StallUpEndRequest;
import com.shinemo.sweepfloor.domain.model.SweepFloorActivityDO;
import com.shinemo.sweepfloor.domain.query.SweepFloorActivityQuery;
import com.shinemo.wangge.core.config.StallUpStateMachine;
import com.shinemo.wangge.dal.mapper.StallUpActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepFloorActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 同步扫楼文件定时任务
 *
 * @author zz
 * @date 2020-06-11
 */
@Slf4j
@Component
public class SynchronizeSweepFloorSchedule {


	@Resource
	private SweepFloorActivityMapper sweepFloorActivityMapper;

	@Scheduled(cron = "0 0 0 * * ? ")
	public void execute() {
		long begin = System.currentTimeMillis();
		log.info("[SynchronizeSweepFloorSchedule] execute start");
		Date dayStartTime = DateUtils.getDayStartTime();
		Date dayEndTime = DateUtils.getDayEndTime();

		SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
		activityQuery.setCreateTime(new Date());
		activityQuery.setStartTime(dayStartTime);
		activityQuery.setEndTime(dayEndTime);
		List<SweepFloorActivityDO> activityDOS = sweepFloorActivityMapper.find(activityQuery);
		if (CollectionUtils.isEmpty(activityDOS)) {
			return;
		}

		log.info("[SynchronizeSweepFloorSchedule] execute end, costTime:{}ms", System.currentTimeMillis() - begin);
	}
}
