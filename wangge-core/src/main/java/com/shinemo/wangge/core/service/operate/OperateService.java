package com.shinemo.wangge.core.service.operate;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.domain.GridInfoToken;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:17
 * @Desc
 */
public interface OperateService {

    ApiResult<Void> addUserOperateLog(UserOperateLogVO userOperateLogVO);

    ApiResult<Void> addUserOperateLogToRedis(UserOperateLogVO userOperateLogVO);

    ApiResult<Void> addUserOperateLogToDB(UserOperateLogVO userOperateLogVO);

    ApiResult<Void> syncLogFromRedisToDB();

    ApiResult<String> genGridInfoToken(GridUserRoleDetail gridDetail);

    //ApiResult<GridInfoToken> validateGridInfoToken(String token,Integer type);
}
