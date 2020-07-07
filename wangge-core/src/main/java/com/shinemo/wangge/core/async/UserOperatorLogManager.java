package com.shinemo.wangge.core.async;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.shinemo.client.async.log.AbstractAsyncLog;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.wangge.core.service.operate.OperateService;

/**
 * @author htdong
 * @date 2020年7月7日 下午7:35:42
 */
public class UserOperatorLogManager extends AbstractAsyncLog<UserOperateLogVO> {

    @Resource
    private OperateService operateService;

    public UserOperatorLogManager(int queueSize, int threadNum, boolean useQueue, long time, TimeUnit unit) {
        super(queueSize, threadNum, useQueue, time, unit);
    }

    @Override
    public void writeLog(UserOperateLogVO tLog) {
        operateService.addUserOperateLog(tLog);
    }

}
