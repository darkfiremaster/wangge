package com.shinemo.schedule.facade;

import com.shinemo.client.common.Result;

/**
 * 类说明:网格 定时任务
 *
 * @author zengpeng
 */
public interface WanggeScheduleFacadeService {

    /**
     * 定时任务:结束摆摊
     * @return
     */
    Result<Void> endStallUp();



    /**
     * 定时任务:获取网格手机号
     * @return
     */
    Result<Void> getGridMobile();


    /**
     * 定时任务:登录信息
     */
    Result<Void> loginInfo();

    /**
     * 定时任务:发送邮件
     */
    Result<Void> sendEmail();
}
