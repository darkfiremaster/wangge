package com.shinemo.wangge.core.service.gridinfo.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.gridinfo.domain.model.SmartGridInfoDO;
import com.shinemo.gridinfo.domain.query.SmartGridInfoQuery;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import com.shinemo.wangge.dal.mapper.SmartGridInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 类说明:
 *
 * @author 曾鹏
 */
@Slf4j
@Service("smartGridInfoService")
public class SmartGridInfoServiceImpl implements SmartGridInfoService {

    @Resource
    private SmartGridInfoMapper smartGridInfoMapper;

    @Override
    public ApiResult<SmartGridInfoDO> getById(Long id) {
        Assert.notNull(id,"id is null");

        SmartGridInfoQuery query = new SmartGridInfoQuery();
        query.setId(id);

        SmartGridInfoDO smartGridInfoDO = smartGridInfoMapper.get(query);
        if(smartGridInfoDO == null){
            log.error("[getByGridId] gridinfo not exits, id:{}",id);
        }
        return ApiResult.success(smartGridInfoDO);
    }

    @Override
    public ApiResult<SmartGridInfoDO> getByGridId(String gridId) {
        Assert.notNull(gridId,"gridId is null");

        SmartGridInfoQuery query = new SmartGridInfoQuery();
        query.setGridId(gridId);

        SmartGridInfoDO smartGridInfoDO = smartGridInfoMapper.get(query);
        if(smartGridInfoDO == null){
            log.error("[getByGridId] gridinfo not exits, gridId:{}",gridId);
        }
        return ApiResult.success(smartGridInfoDO);

    }
}
