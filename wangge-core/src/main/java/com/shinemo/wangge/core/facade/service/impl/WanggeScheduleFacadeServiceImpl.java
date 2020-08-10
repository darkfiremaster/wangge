package com.shinemo.wangge.core.facade.service.impl;

import com.shinemo.client.common.Result;
import com.shinemo.schedule.facade.WanggeScheduleFacadeService;
import com.shinemo.wangge.core.schedule.EndStallUpSchedule;
import com.shinemo.wangge.core.schedule.GetGridMobileSchedule;
import com.shinemo.wangge.core.schedule.LoginInfoSchedule;
import com.shinemo.wangge.core.schedule.SendMailSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 类说明:类说明: 定时任务FacadeService实现类
 *
 * @author zengpeng
 */
@Slf4j
@Service("wanggeScheduleFacadeService")
public class WanggeScheduleFacadeServiceImpl implements WanggeScheduleFacadeService {
    @Resource
    private EndStallUpSchedule endStallUpSchedule;

    @Resource
    private GetGridMobileSchedule getGridMobileSchedule;


    @Resource
    private LoginInfoSchedule loginInfoSchedule;

    @Resource
    private SendMailSchedule sendMailSchedule;

    @Override
    @Async
    public Result<Void> endStallUp() {
        endStallUpSchedule.execute();
        return Result.success();
    }

    @Override
    @Async
    public Result<Void> getGridMobile() {
        getGridMobileSchedule.execute();
        return Result.success();
    }



    @Override
    @Async
    public Result<Void> loginInfo() {
        loginInfoSchedule.generateLoginInfoResult();
        return Result.success();
    }

    @Override
    @Async
    public Result<Void> sendEmail() {
        sendMailSchedule.sendLoginMail();
        return Result.success();
    }

    @Override
    @Async
    public Result<Void> test1() {
        log.info("[test1] threadName: " + Thread.currentThread().getName());
        return Result.success();
    }

    @Override
    public Result<Void> test2() {
        log.info("[test2] threadName: " + Thread.currentThread().getName());
        sendMailSchedule.testSend();
        return Result.success();
    }

    @Override
    public Result<Void> test3() {
        log.info("[test3] threadName: " + Thread.currentThread().getName());
        return Result.success();
    }
}
