package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.query.LoginInfoResultQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface LoginInfoResultMapper extends BaseMapper<LoginInfoResultQuery, LoginInfoResultDO> {

    Long insertBatch(@Param("loginInfoResultDOList") List<LoginInfoResultDO> loginInfoResultDOList);

}
