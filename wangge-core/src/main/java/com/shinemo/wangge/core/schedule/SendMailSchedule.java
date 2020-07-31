package com.shinemo.wangge.core.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.wangge.core.service.common.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author shangkaihui
 * @Date 2020/7/17 14:33
 * @Desc
 */
@Component
@Slf4j
public class SendMailSchedule {

    @Autowired
    private ExcelService excelService;

    @NacosValue(value = "${open.sendMail}", autoRefreshed = true)
    private Boolean openSendMail = false;

    /**
     * 每天10点触发
     */
//    @Scheduled(cron = "0 0 10 * * ?")
    public void sendLoginMail() {
        if (openSendMail) {
            DateTime yesterday = DateUtil.yesterday();
            String queryDate = DateUtil.formatDate(yesterday);
            log.info("[sendLoginMail] 开始发送邮件,查询时间:{}", queryDate);
            long startTime = System.currentTimeMillis();
            excelService.sendLoginInfoMail(queryDate);
            log.info("[sendLoginMail] 结束邮件发送,耗时:{}ms", System.currentTimeMillis() - startTime);
        }
    }
}
