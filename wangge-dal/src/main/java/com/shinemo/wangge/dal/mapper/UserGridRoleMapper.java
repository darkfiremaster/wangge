package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.UserGridRoleDO;
import com.shinemo.operate.query.UserGridRoleQuery;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface UserGridRoleMapper extends BaseMapper<UserGridRoleQuery, UserGridRoleDO> {

    void delete(UserGridRoleQuery userGridRoleQuery);
}
