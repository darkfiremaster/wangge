package com.shinemo.wangge.core.service.gridinfo.impl;

import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.gridinfo.domain.model.SmartGridInfoDO;
import com.shinemo.gridinfo.domain.query.SmartGridInfoQuery;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.dal.mapper.SmartGridInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private HuaWeiService huaWeiService;

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
        return ApiResult.of(0,smartGridInfoDO);

    }

    @Override
    public List<GridUserRoleDetail> getGridUserRole(String mobile) {
        HuaWeiRequest huaWeiRequest = HuaWeiRequest.builder().mobile(mobile).build();
        try {
            ApiResult<List<GridUserRoleDetail>> apiResult = huaWeiService.getGridUserInfo(huaWeiRequest);
            if (!apiResult.isSuccess()) {
                log.error("[getGridUserRole] huaWeiService.getGridUserInfo not success,apiResult = {}," + "mobile = {}",
                        apiResult, mobile);
                return null;
            }
            return apiResult.getData();
        } catch (ApiException e) {
            log.error("[getGridUserRole] huaWeiService.getGridUserInfo error,msg = {},mobile = {}", e.getMessage(),
                    mobile);
            return null;
        }
    }
}
