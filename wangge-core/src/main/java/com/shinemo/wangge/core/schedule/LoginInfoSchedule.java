package com.shinemo.wangge.core.schedule;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.wangge.core.service.operate.LoginStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Scheduled(cron = "0 0 1 * * ?")
    public void generateLoginInfoResult() {
        log.info("[generateLoginInfoResult] 开始统计登录信息, ");
        long startTime = System.currentTimeMillis();
        ApiResult<List<LoginInfoResultDO>> listApiResult = loginStatisticsService.saveYesterdayLoginInfoResult();
        log.info("[generateLoginInfoResult] 结束统计登录信息,耗时:{}ms,生成数据:{}", System.currentTimeMillis() - startTime,listApiResult.getData().size());

    }
}
