package com.shinemo.wangge.core.push;

import com.shinemo.wangge.core.push.domain.PushMsgExtra;

/**
 * @Date: Created by Zeng Pingping on 12 / 12 / 2018
 * @Author: zengpp@shinemo.com
 * @Description:
 */
public interface PushService {

    void push(PushMsgExtra extra, int appType, long receiver);

}
