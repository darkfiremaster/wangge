package com.shinemo.wangge.core.delay;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Sms.SmsService;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
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
public class DaoSanJiaoWarnDelayJobExecutor implements DelayJobExecutor {

    @Resource
    private ThirdTodoMapper thirdTodoMapper;

    @Resource
    private SmsService smsService;

    @Override
    public void execute(DelayJob job) {
        log.info("[execute] 开始执行任务:{}", job);

        Map<String, Object> jobParams = job.getJobParams();
        Long id = MapUtil.getLong(jobParams, "id");
        Assert.notNull(id, "id is null");

        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setId(id);
        TodoDO todoDO = thirdTodoMapper.get(todoQuery);

        if (todoDO == null) {
            return;
        }

        //判断工单的状态,如果是已执行,则不发送
        if (todoDO.getStatus().equals(0)) {
            // 发短信
            ArrayList<String> mobile = new ArrayList<>();
            mobile.add(todoDO.getOperatorMobile());
            ArrayList<String> contents = new ArrayList<>();
            ArrayList<String> successMobiles = new ArrayList<>();
            int ret = smsService.sendSms(mobile, 234, AppTypeEnum.GXNB.getId(), contents, successMobiles, new AaceContext());
            if (ret == 0) {
                log.info("[sendSms] 发送短信成功,mobile:{}", mobile);
            } else {
                log.error("[sendSms] 发送短信失败,mobile:{}", mobile);
            }
        }


    }

}
