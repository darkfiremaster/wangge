package com.shinemo.wangge.core.service.operate;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:17
 * @Desc
 */
public interface OperateService {

    ApiResult<Void> addUserOperateLog(UserOperateLogVO userOperateLogVO);



}
