package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.operate.excel.LoginInfoExcelDTO;
import com.shinemo.operate.query.UserOperateLogQuery;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface UserOperateLogMapper extends BaseMapper<UserOperateLogQuery, UserOperateLogDO> {
    List<LoginInfoExcelDTO> getLoginInfoExcelDTOList(UserOperateLogQuery userOperateLogQuery);

}
