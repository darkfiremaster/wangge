package com.shinemo.wangge.core.service.operate;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.operate.excel.LoginInfoExcelDTO;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 11:00
 * @Desc
 */
public interface LoginStatisticsService {

    ApiResult<List<LoginInfoResultDO>> saveYesterdayLoginInfoResult();

    ApiResult<List<LoginInfoExcelDTO>> getLoginInfoExcelDTOList();
}
