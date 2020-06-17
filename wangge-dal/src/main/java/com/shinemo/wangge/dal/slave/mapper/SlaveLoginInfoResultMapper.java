package com.shinemo.wangge.dal.slave.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.excel.LoginInfoExcelDTO;
import com.shinemo.operate.query.LoginInfoResultQuery;
import com.shinemo.operate.query.UserOperateLogQuery;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface SlaveLoginInfoResultMapper extends BaseMapper<LoginInfoResultQuery, LoginInfoResultDO> {

    List<LoginInfoResultDO> getLoginInfoResultDOList(LoginInfoResultQuery loginInfoResultQuery);

    List<LoginInfoResultDO> getBeforeYesterdayLoginInfoResultList();

    List<LoginInfoExcelDTO> getLoginInfoExcelDTOList(UserOperateLogQuery userOperateLogQuery);

}
