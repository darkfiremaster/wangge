package com.shinemo.wangge.core.service.operate;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:17
 * @Desc
 */
public interface OperateService {

    ApiResult<Void> addUserOperateLog(UserOperateLogVO userOperateLogVO);


    void test();
}
