package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.query.LoginInfoResultQuery;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface LoginInfoResultMapper extends BaseMapper<LoginInfoResultQuery, LoginInfoResultDO> {

    List<LoginInfoResultDO> getLoginInfoResultDOList(LoginInfoResultQuery loginInfoResultQuery);

    Long insertBatch(List<LoginInfoResultDO> loginInfoResultDOList);

    List<LoginInfoResultDO> getYesterdayLoginInfoResultList();

}
