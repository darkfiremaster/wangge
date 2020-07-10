package com.shinemo.wangge.dal.slave.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.excel.LoginInfoExcelDTO;
import com.shinemo.excel.LoginResultExcelDTO;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.query.LoginInfoResultQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 13:56
 * @Desc
 */
public interface SlaveLoginInfoResultMapper extends BaseMapper<LoginInfoResultQuery, LoginInfoResultDO> {

    List<LoginInfoResultDO> getLoginInfoResultDOList(LoginInfoResultQuery loginInfoResultQuery);

    List<LoginInfoResultDO> getBeforeYesterdayLoginInfoResultList();


    List<LoginResultExcelDTO> getLoginResultExcelDTOList(@Param("date") String date);
    List<LoginInfoExcelDTO> getLoginInfoExcelDTOList(@Param("date") String date, @Param("tableIndex") String tableIndex);
}
