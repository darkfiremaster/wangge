package com.shinemo.wangge.core.schedule;

import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 19:10
 * @Desc
 */
@Component
@Slf4j
public class LoginInfoSchedule {

    @Autowired
    private LoginStatisticsService loginStatisticsService;

    public void generateLoginInfoResult() {
       loginStatisticsService.saveYesterdayLoginInfoResult();
    }
}
