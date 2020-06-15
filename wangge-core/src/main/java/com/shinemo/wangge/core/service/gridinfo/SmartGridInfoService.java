package com.shinemo.wangge.core.service.gridinfo;

import com.shinemo.client.common.Result;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.gridinfo.domain.model.SmartGridInfoDO;

/**
 * 类说明: 网格信息接口
 *
 * @author 曾鹏
 */
public interface SmartGridInfoService {
    /**
     * 通过id获取网格信息
     * @param id
     * @return
     */
    ApiResult<SmartGridInfoDO> getById(Long id);

    /**
     * 通过网格编码获取网格信息
     * @param gridId
     * @return
     */
    ApiResult<SmartGridInfoDO> getByGridId(String gridId);

}
