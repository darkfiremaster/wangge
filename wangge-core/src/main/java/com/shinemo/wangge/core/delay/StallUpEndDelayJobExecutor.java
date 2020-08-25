package com.shinemo.wangge.core.delay;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;
import com.shinemo.wangge.dal.mapper.StallUpActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author shangkaihui
 * @Date 2020/8/24 15:38
 * @Desc
 */
@Component
@Slf4j
public class StallUpEndDelayJobExecutor implements DelayJobExecutor {

    @Resource
    private StallUpActivityMapper stallUpActivityMapper;

    @Override
    public void execute(DelayJob job) {
        Map<String, Object> jobParams = job.getJobParams();
        log.info("[StallUpEndDelayJobExecute.execute] 开始执行任务:{}", jobParams);

        //判断活动是否是未开始
        Long activityId = MapUtil.getLong(jobParams, "id");
        Assert.notNull(activityId, "活动id不能为空");

        StallUpActivityQuery stallUpActivityQuery = new StallUpActivityQuery();
        stallUpActivityQuery.setId(activityId);
        StallUpActivity stallUpActivity = stallUpActivityMapper.get(stallUpActivityQuery);

        if (stallUpActivity.getStatus().equals(StallUpStatusEnum.PREPARE.getId()) || stallUpActivity.getStatus().equals(StallUpStatusEnum.STARTED.getId())) {
            //todo 发短信
            log.info("摆摊活动未结束,发送短信,提醒用户");
        }

    }
}
