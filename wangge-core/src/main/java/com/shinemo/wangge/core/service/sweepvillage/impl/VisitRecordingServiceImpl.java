package com.shinemo.wangge.core.service.sweepvillage.impl;


import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;
import com.shinemo.sweepvillage.domain.request.VisitRecordingListRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.sweepvillage.VisitRecordingService;
import com.shinemo.wangge.dal.mapper.SmartGridActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageVisitRecordingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VisitRecordingServiceImpl implements VisitRecordingService {

    @Resource
    private SweepVillageVisitRecordingMapper sweepVillageVisitRecordingMapper;
    @Resource
    private HuaWeiService huaWeiService;
    @Resource
    private SmartGridActivityMapper smartGridActivityMapper;

    @Override
    public ApiResult<Void> add(SweepVillageVisitRecordingVO request) {
        String mobile = SmartGridContext.getMobile();
        SweepVillageVisitRecordingDO visitRecordingDO = new SweepVillageVisitRecordingDO();
        BeanUtils.copyProperties(request,visitRecordingDO);
        visitRecordingDO.setMobile(mobile);
        sweepVillageVisitRecordingMapper.insert(visitRecordingDO);
        //todo 同步华为

        return ApiResult.of(0);
    }

    @Override
    public ApiResult<Void> update(SweepVillageVisitRecordingVO request) {
        //权限只有本人以及网格长
        SweepVillageVisitRecordingDO visitRecordingDO = getDo(request.getId(), 1);

        if (visitRecordingDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.VISIT_RECORDING_NOT_EXIST);
        }

        if (!checkAuth((visitRecordingDO))) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.VISIT_RECORDING_UPDATE_NOT_AUTH);
        }

        SweepVillageVisitRecordingDO updateVisitRecordingDO = new SweepVillageVisitRecordingDO();
        BeanUtils.copyProperties(request,updateVisitRecordingDO);
        sweepVillageVisitRecordingMapper.update(updateVisitRecordingDO);

        return ApiResult.of(0);
    }

    @Override
    public ApiResult<List<SweepVillageVisitRecordingVO>> getVisitRecordingByTenantsId(VisitRecordingListRequest request) {
        SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
        visitRecordingQuery.setStatus(1);
        visitRecordingQuery.setTenantsId(request.getTenantsId());
        List<SweepVillageVisitRecordingDO> doList = sweepVillageVisitRecordingMapper.find(visitRecordingQuery);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0,new ArrayList<>());
        }
        List<SweepVillageVisitRecordingVO> vos = new ArrayList<>(doList.size());
        for (SweepVillageVisitRecordingDO visitRecordingDO:doList) {
            SweepVillageVisitRecordingVO visitRecordingVO = new SweepVillageVisitRecordingVO();
            BeanUtils.copyProperties(visitRecordingDO,visitRecordingVO);
            if (visitRecordingDO.getActivityId().equals(request.getActivityId())) {
                visitRecordingVO.setType(1);
            }else {
                visitRecordingVO.setType(2);
            }
            visitRecordingVO.setVisitTime(visitRecordingDO.getGmtCreate());
            vos.add(visitRecordingVO);
        }
        return ApiResult.of(0,vos);
    }

    @Override
    public ApiResult<ListVO<SweepVillageVisitRecordingVO>> getVisitRecordingByActivityId(VisitRecordingListRequest request) {
        SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
        visitRecordingQuery.setStatus(1);
        visitRecordingQuery.setActivityId(request.getActivityId());
        visitRecordingQuery.setCurrentPage(request.getCurrentPage());
        visitRecordingQuery.setPageSize(request.getPageSize());
        visitRecordingQuery.setQueryTotal(false);
        List<SweepVillageVisitRecordingDO> doList = sweepVillageVisitRecordingMapper.find(visitRecordingQuery);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0,ListVO.list(new ArrayList<>(),0));
        }
        List<SweepVillageVisitRecordingVO> vos = new ArrayList<>();
        for (SweepVillageVisitRecordingDO visitRecordingDO: doList) {
            SweepVillageVisitRecordingVO visitRecordingVO = new SweepVillageVisitRecordingVO();
            BeanUtils.copyProperties(visitRecordingDO,visitRecordingVO);
            visitRecordingVO.setVisitTime(visitRecordingDO.getGmtCreate());
            vos.add(visitRecordingVO);
        }
        SweepVillageVisitRecordingQuery countQuery = new SweepVillageVisitRecordingQuery();
        countQuery.setStatus(1);
        countQuery.setActivityId(request.getActivityId());
        long count = sweepVillageVisitRecordingMapper.count(countQuery);
        return ApiResult.of(0,ListVO.list(vos,count));
    }

    private SweepVillageVisitRecordingDO getDo(Long id,Integer status) {
        SweepVillageVisitRecordingQuery query = new SweepVillageVisitRecordingQuery();
        query.setId(id);
        query.setStatus(status);
        return sweepVillageVisitRecordingMapper.get(query);
    }


    private boolean checkAuth(SweepVillageVisitRecordingDO visitRecordingDO) {
        String queryMobile = SmartGridContext.getMobile();
        if (!visitRecordingDO.getMobile().equals(queryMobile)) {
            return false;
        }
        HuaWeiRequest huaWeiRequest = HuaWeiRequest.builder().mobile(SmartGridContext.getMobile()).build();
        ApiResult<List<GridUserRoleDetail>> apiResult = huaWeiService.getGridUserInfo(huaWeiRequest);
        if (apiResult.isSuccess()) {
            List<GridUserRoleDetail> roleDetails = apiResult.getData();
            if (CollectionUtils.isEmpty(roleDetails)) {
                return false;
            }
            //todo 只有对应网格长才可以编辑

        }
        return true;
    }

}
