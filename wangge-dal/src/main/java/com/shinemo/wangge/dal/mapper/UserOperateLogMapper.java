package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.operate.query.UserOperateLogQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface UserOperateLogMapper extends BaseMapper<UserOperateLogQuery, UserOperateLogDO> {

    Long insertBatch(@Param("logDOList") List<UserOperateLogDO> logDOList, @Param("tableIndex") String tableIndex);
}
