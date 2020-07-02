package com.shinemo.wangge.core.service.sweepvillage.impl;


import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.sweepfloor.domain.model.SmartGridActivityDO;
import com.shinemo.sweepfloor.domain.query.SmartGridActivityQuery;
import com.shinemo.sweepvillage.domain.model.SweepVillageActivityDO;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;
import com.shinemo.sweepvillage.domain.request.VisitRecordingListRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.sweepvillage.domain.query.SweepVillageActivityQuery;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.sweepvillage.VisitRecordingService;
import com.shinemo.wangge.dal.mapper.SmartGridActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageVisitRecordingMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VisitRecordingServiceImpl implements VisitRecordingService {

    @Resource
    private SweepVillageVisitRecordingMapper sweepVillageVisitRecordingMapper;
    @Resource
    private HuaWeiService huaWeiService;
    @Resource
    private SmartGridActivityMapper smartGridActivityMapper;
    @Resource
    private SweepVillageActivityMapper sweepVillageActivityMapper;

    private static final String GRID_MANAGER_ROLE = "1";

    @Override
    public ApiResult<Void> add(SweepVillageVisitRecordingVO request) {
        String mobile = SmartGridContext.getMobile();
        String userName = SmartGridContext.getUserName();
        SweepVillageVisitRecordingDO visitRecordingDO = new SweepVillageVisitRecordingDO();
        BeanUtils.copyProperties(request,visitRecordingDO);
        visitRecordingDO.setMobile(mobile);
        visitRecordingDO.setMarketingUserName(userName);
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
            convertBusinessType(visitRecordingDO,visitRecordingVO);
            vos.add(visitRecordingVO);
        }
        SweepVillageVisitRecordingQuery countQuery = new SweepVillageVisitRecordingQuery();
        countQuery.setStatus(1);
        countQuery.setActivityId(request.getActivityId());
        long count = sweepVillageVisitRecordingMapper.count(countQuery);
        return ApiResult.of(0,ListVO.list(vos,count));
    }

    @Override
    public ApiResult<SweepVillageVisitRecordingVO> getVisitRecordingDetail(Long id) {
        SweepVillageVisitRecordingDO recordingDO = getDo(id, 1);
        if (recordingDO == null) {
            return ApiResultWrapper.fail(SweepVillageErrorCodes.VISIT_RECORDING_NOT_EXIST);
        }
        SweepVillageVisitRecordingVO visitRecordingVO = new SweepVillageVisitRecordingVO();
        BeanUtils.copyProperties(recordingDO,visitRecordingVO);
        visitRecordingVO.setVisitTime(recordingDO.getGmtCreate());
        convertBusinessType(recordingDO,visitRecordingVO);

        if (checkAuth(recordingDO)) {
            visitRecordingVO.setUpdateAndDeleteButtonFlag(1);
        }else {
            visitRecordingVO.setUpdateAndDeleteButtonFlag(0);
        }
        return ApiResult.of(0,visitRecordingVO);
    }

    private void convertBusinessType(SweepVillageVisitRecordingDO recordingDO,SweepVillageVisitRecordingVO visitRecordingVO) {
        if (!StringUtils.isBlank(recordingDO.getBusinessType())) {
            visitRecordingVO.setBusinessType(GsonUtils.fromJsonToList(recordingDO.getBusinessType(), StallUpBizType[].class));
        }else {
            visitRecordingVO.setBusinessType(new ArrayList<>());
        }
    }

    private SweepVillageVisitRecordingDO getDo(Long id,Integer status) {
        SweepVillageVisitRecordingQuery query = new SweepVillageVisitRecordingQuery();
        query.setId(id);
        query.setStatus(status);
        return sweepVillageVisitRecordingMapper.get(query);
    }


    private boolean checkAuth(SweepVillageVisitRecordingDO visitRecordingDO) {
        String queryMobile = SmartGridContext.getMobile();
        if (!visitRecordingDO.getMobile().equals(queryMobile) && !isGridManager(visitRecordingDO)) {
            return false;
        }else {
            return true;
        }
    }

    //todo  走访记录的网格信息从扫村主表获取
    private boolean isGridManager(SweepVillageVisitRecordingDO visitRecordingDO) {
        String gridInfo = SmartGridContext.getGridInfo();
        List<GridUserRoleDetail> details = GsonUtils.fromJsonToList(gridInfo, GridUserRoleDetail[].class);
        SweepVillageActivityQuery activityQuery = new SweepVillageActivityQuery();
        activityQuery.setId(visitRecordingDO.getActivityId());
        SweepVillageActivityDO activityDO = sweepVillageActivityMapper.get(activityQuery);
        if (activityDO == null) {
            log.error("[checkAuth] update visit record error activityDO is null,activityId = {}",visitRecordingDO.getActivityId());
            return false;
        }
        SmartGridActivityQuery gridActivityQuery = new SmartGridActivityQuery();
        gridActivityQuery.setActivityId(activityDO.getId());
        List<SmartGridActivityDO> gridActivityDOS = smartGridActivityMapper.find(gridActivityQuery);
        if (CollectionUtils.isEmpty(gridActivityDOS)) {
            return false;
        }
        //List<String> dbGridIds = gridActivityDOS.stream().map(SmartGridActivityDO::getGridId).collect(Collectors.toList());
        Map<String, GridUserRoleDetail> roleDetailMap = details.stream().collect(Collectors.toMap(GridUserRoleDetail::getId, a -> a, (k1, k2) -> k1));
        for (SmartGridActivityDO gridActivityDO : gridActivityDOS) {
            GridUserRoleDetail detail = roleDetailMap.get(gridActivityDO.getGridId());
            if (detail != null) {
                List<GridUserRoleDetail.GridRole> roleList = detail.getRoleList();
                if (!CollectionUtils.isEmpty(roleList)) {
                    for (GridUserRoleDetail.GridRole role: roleList) {
                        if (role.getId().equals(GRID_MANAGER_ROLE)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
