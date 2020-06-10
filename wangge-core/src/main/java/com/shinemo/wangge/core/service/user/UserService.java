package com.shinemo.wangge.core.service.user;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 19:18
 * @Desc
 */
public interface UserService {

    ApiResult<Void> updateUserGridRoleRelation(List<GridUserRoleDetail> gridUserRoleDetailList, String mobile);

}
