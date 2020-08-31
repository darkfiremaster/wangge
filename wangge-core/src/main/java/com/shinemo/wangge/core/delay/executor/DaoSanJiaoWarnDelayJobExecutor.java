package com.shinemo.wangge.core.delay.executor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Sms.SmsService;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.smartgrid.enums.SmsTemplateEnum;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.enums.TodoStatusEnum;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.wangge.core.delay.DelayJob;
import com.shinemo.wangge.core.delay.DelayJobExecutor;
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
        log.info("[倒三角工单超时提醒] 开始执行任务:{}", job);

        Map<String, Object> jobParams = job.getJobParams();
        Long id = MapUtil.getLong(jobParams, "id");
        Assert.notNull(id, "id is null");

        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setId(id);
        TodoDO todoDO = thirdTodoMapper.get(todoQuery);

        if (todoDO == null) {
            log.error("[execute] 倒三角工单不存在,id:{}",id);
            return;
        }

        //判断工单的状态,如果是已执行,则不发送
        if (todoDO.getStatus().equals(TodoStatusEnum.NOT_FINISH.getId())) {
            // 发短信
            ArrayList<String> mobile = new ArrayList<>();
            mobile.add(todoDO.getOperatorMobile());
            ArrayList<String> contents = new ArrayList<>();
            ArrayList<String> successMobiles = new ArrayList<>();
            int ret = smsService.sendSms(mobile, SmsTemplateEnum.DAOSANJIAO_WARN.getTemplateId(), AppTypeEnum.GXNB.getId(), contents, successMobiles, new AaceContext());
            if (ret == 0) {
                log.info("[sendSms] 发送短信成功,mobile:{},工单id:{}", todoDO.getOperatorMobile(),todoDO.getThirdId());
            } else {
                log.info("[sendSms] 发送短信失败,mobile:{},工单id:{}", todoDO.getOperatorMobile(),todoDO.getThirdId());
            }
        }


    }

}
