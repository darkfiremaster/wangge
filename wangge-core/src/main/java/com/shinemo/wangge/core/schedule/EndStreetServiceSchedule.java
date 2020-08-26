package com.shinemo.wangge.core.schedule;

import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import com.shinemo.groupserviceday.enums.GroupServiceDayStatusEnum;
import com.shinemo.sweepstreet.domain.model.SweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.query.SweepStreetActivityQuery;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
import com.shinemo.wangge.dal.mapper.SweepStreetActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时关闭扫街活动
 * @author ChenQinHai
 * @date 2020/8/17
 */
@Slf4j
@Component
public class EndStreetServiceSchedule {
    @Resource
    private SweepStreetActivityMapper sweepStreetActivityMapper;
    @Resource
    private SweepStreetService sweepStreetService;

    @Scheduled(cron = "0 0 0 * * ? ")
    //@Scheduled(cron = "0 */1 * * * ?")
    public void execute() {
        long begin = System.currentTimeMillis();
        log.info("[EndStallUpSchedule] execute start");

        SweepStreetActivityQuery query = new SweepStreetActivityQuery();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(GroupServiceDayStatusEnum.NOT_START.getId());
        statusList.add(GroupServiceDayStatusEnum.PROCESSING.getId());
        query.setStatusList(statusList);
        query.setPageEnable(false);
        List<SweepStreetActivityDO> sweepStreetActivityDOS = sweepStreetActivityMapper.find(query);
        for (SweepStreetActivityDO streetActivityDO: sweepStreetActivityDOS) {
            if (streetActivityDO.getPlanEndTime().getTime() < begin) {
                //自动结束
                sweepStreetService.autoEnd(streetActivityDO);
            }
        }

        log.info("[EndStallUpSchedule] execute end, costTime:{}ms", System.currentTimeMillis() - begin);
    }
}
