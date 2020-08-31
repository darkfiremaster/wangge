package com.shinemo.wangge.core.task;

import cn.hutool.extra.spring.SpringUtil;
import com.shinemo.Aace.context.AaceContext;
import com.shinemo.client.ace.Sms.SmsService;
import com.shinemo.client.order.AppTypeEnum;
import com.shinemo.smartgrid.enums.SmsTemplateEnum;
import com.shinemo.todo.dto.TodoCountDTO;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/8/28 14:12
 * @Desc
 */
@NoArgsConstructor
@Slf4j
public class YuJingTimeoutWarnTask implements Runnable {

    private List<TodoCountDTO> todoCountDTO;



    public YuJingTimeoutWarnTask(List<TodoCountDTO> todoCountDTO) {
        this.todoCountDTO = todoCountDTO;
    }

    @SneakyThrows
    @Override
    public void run() {
        log.info("线程:{}开始发送短信", Thread.currentThread().getName());
        SmsService smsService = SpringUtil.getBean("smsService");
        for (TodoCountDTO countDTO : todoCountDTO) {
            try {
                // 发短信
                ArrayList<String> mobile = new ArrayList<>();
                mobile.add(countDTO.getMobile());
                ArrayList<String> contents = new ArrayList<>();
                contents.add(String.valueOf(countDTO.getTodoCount()));
                ArrayList<String> successMobiles = new ArrayList<>();
                int ret = smsService.sendSms(mobile, SmsTemplateEnum.YUJING_TIMEOUT_WARN.getTemplateId(), AppTypeEnum.GXNB.getId(), contents, successMobiles, new AaceContext());
                if (ret == 0) {
                    log.info("[sendSms] 发送短信成功,mobile:{},工单数量:{}", countDTO.getMobile(),countDTO.getTodoCount());
                } else {
                    log.info("[sendSms] 发送短信成功,mobile:{},工单数量:{}", countDTO.getMobile(),countDTO.getTodoCount());
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("发送短信失败,失败原因:{},手机号:{}", e.getMessage(), countDTO.getMobile());
            }
        }
        log.info("线程:{}完成发送短信", Thread.currentThread().getName());
    }
}
