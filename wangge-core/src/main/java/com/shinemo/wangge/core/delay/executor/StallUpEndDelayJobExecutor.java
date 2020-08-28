package com.shinemo.wangge.core.delay.executor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Sms.SmsService;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.smartgrid.enums.SmsTemplateEnum;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;
import com.shinemo.wangge.core.delay.DelayJob;
import com.shinemo.wangge.core.delay.DelayJobExecutor;
import com.shinemo.wangge.dal.mapper.StallUpActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private SmsService smsService;

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
            //发短信
            ArrayList<String> mobile = new ArrayList<>();
            mobile.add(stallUpActivity.getMobile());
            ArrayList<String> contents = new ArrayList<>();
            contents.add(stallUpActivity.getTitle());
            ArrayList<String> successMobiles = new ArrayList<>();
            int ret = smsService.sendSms(mobile, SmsTemplateEnum.STALL_UP_END.getTemplateId(), AppTypeEnum.GXNB.getId(), contents, successMobiles, new AaceContext());
            if (ret == 0) {
                log.info("[sendSms] 发送短信成功,mobile:{}", mobile);
            } else {
                log.error("[sendSms] 发送短信失败,mobile:{}", mobile);
            }
        }

    }
}
