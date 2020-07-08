package com.shinemo.wangge.core.async;

import com.shinemo.client.async.log.AbstractAsyncLog;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.wangge.dal.mapper.UserOperateLogMapper;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author htdong
 * @date 2020年7月7日 下午7:35:42
 */
public class UserOperatorLogManager extends AbstractAsyncLog<UserOperateLogDO> {


    @Resource
    private UserOperateLogMapper userOperateLogMapper;

    public UserOperatorLogManager(int queueSize, int threadNum, boolean useQueue, long time, TimeUnit unit) {
        super(queueSize, threadNum, useQueue, time, unit);
    }

    @Override
    public void writeLog(UserOperateLogDO tLog) {
        userOperateLogMapper.insert(tLog);
    }

}
