package com.shinemo.wangge.core.service.sweepvillage.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shinemo.client.common.ListVO;
import com.shinemo.client.common.StatusEnum;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.SmartGridUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.model.SimpleStallUpBizType;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.sweepfloor.common.enums.HuaweiSweepFloorUrlEnum;
import com.shinemo.sweepfloor.common.enums.SweepFloorStatusEnum;
import com.shinemo.sweepfloor.domain.model.SmartGridActivityDO;
import com.shinemo.sweepfloor.domain.model.SweepFloorActivityDO;
import com.shinemo.sweepfloor.domain.query.SmartGridActivityQuery;
import com.shinemo.sweepvillage.domain.constant.SweepVillageConstants;
import com.shinemo.sweepvillage.domain.enums.HuaweiSweepVillageUrlEnum;
import com.shinemo.sweepvillage.domain.model.SweepVillageActivityDO;
import com.shinemo.sweepvillage.domain.model.SweepVillageVisitRecordingDO;
import com.shinemo.sweepvillage.domain.query.SweepVillageVisitRecordingQuery;
import com.shinemo.sweepvillage.domain.request.VisitRecordingListRequest;
import com.shinemo.sweepvillage.domain.vo.SweepVillageVisitRecordingVO;
import com.shinemo.sweepvillage.error.SweepVillageErrorCodes;
import com.shinemo.sweepvillage.domain.query.SweepVillageActivityQuery;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.sweepvillage.VisitRecordingService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.SmartGridActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageActivityMapper;
import com.shinemo.wangge.dal.mapper.SweepVillageVisitRecordingMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
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

    private static final String ID_PREFIX = "SC_RECORD_";

    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    @Override
    public ApiResult<Void> add(SweepVillageVisitRecordingVO request) {
        String mobile = SmartGridContext.getMobile();
        String userName = SmartGridContext.getUserName();
        SweepVillageVisitRecordingDO visitRecordingDO = new SweepVillageVisitRecordingDO();
        BeanUtils.copyProperties(request,visitRecordingDO);
        visitRecordingDO.setMobile(mobile);
        visitRecordingDO.setMarketingUserName(userName);

        String businessType = GsonUtil.toJson(request.getBizTypes());
        visitRecordingDO.setBusinessType(businessType);
        sweepVillageVisitRecordingMapper.insert(visitRecordingDO);
        //todo 同步华为
        synchronizeSweepingData(visitRecordingDO,HuaweiSweepVillageUrlEnum.ADD_SWEEPING_VILLAGE_DATA.getApiName());
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
        //删除
        if(request.getStatus() != null && request.getStatus() == StatusEnum.DELETE.getId()){
            sweepVillageVisitRecordingMapper.update(updateVisitRecordingDO);
            synchronizeSweepingData(updateVisitRecordingDO,HuaweiSweepVillageUrlEnum.DELETE_SWEEPING_VILLAGE_DATA.getApiName());
            return ApiResult.of(0);
        }
        updateVisitRecordingDO.setBusinessType(GsonUtil.toJson(request.getBizTypes()));
        sweepVillageVisitRecordingMapper.update(updateVisitRecordingDO);
        synchronizeSweepingData(updateVisitRecordingDO,HuaweiSweepVillageUrlEnum.UPDATE_SWEEPING_VILLAGE_DATA.getApiName());
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
                visitRecordingVO.setType(0);
            }
            visitRecordingVO.setVisitTime(visitRecordingDO.getGmtCreate());
            vos.add(visitRecordingVO);
        }
        return ApiResult.of(0,vos);
    }

    @Override
    public ApiResult<List<SweepVillageVisitRecordingVO>> getVisitRecordingByActivityId(VisitRecordingListRequest request) {
        SweepVillageVisitRecordingQuery visitRecordingQuery = new SweepVillageVisitRecordingQuery();
        visitRecordingQuery.setStatus(1);
        visitRecordingQuery.setActivityId(request.getActivityId());
        List<SweepVillageVisitRecordingDO> doList = sweepVillageVisitRecordingMapper.find(visitRecordingQuery);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0,new ArrayList<>());
        }
        String mobile = SmartGridContext.getMobile();
        List<SweepVillageVisitRecordingVO> vos = new ArrayList<>();
        for (SweepVillageVisitRecordingDO visitRecordingDO: doList) {
            SweepVillageVisitRecordingVO visitRecordingVO = new SweepVillageVisitRecordingVO();
            BeanUtils.copyProperties(visitRecordingDO,visitRecordingVO);
            visitRecordingVO.setVisitTime(visitRecordingDO.getGmtCreate());
            convertBusinessType(visitRecordingDO,visitRecordingVO);
            if (!StringUtils.isBlank(visitRecordingDO.getCreateTenantsMobile())
                    && !visitRecordingDO.getCreateTenantsMobile().equals(mobile)) {
                //脱敏
                String desensitizationMobile = SmartGridUtils.desensitizationMobile(visitRecordingDO.getContactMobile());
                String desensitizationName = SmartGridUtils.desensitizationName(visitRecordingDO.getContactName());
                visitRecordingVO.setContactMobile(desensitizationMobile);
                visitRecordingVO.setContactName(desensitizationName);
            }
            vos.add(visitRecordingVO);
        }
        return ApiResult.of(0,vos);
    }

    @Override
    public ApiResult<SweepVillageVisitRecordingVO> getVisitRecordingDetail(Long id) {
        SweepVillageVisitRecordingDO recordingDO = getDo(id, 1);
        if (recordingDO == null || recordingDO.getStatus() == 0) {
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
            visitRecordingVO.setBizTypes(GsonUtils.fromJsonToList(recordingDO.getBusinessType(), StallUpBizType[].class));
        }else {
            visitRecordingVO.setBizTypes(new ArrayList<>());
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
        String gridId = activityDO.getGridId();
        Map<String, GridUserRoleDetail> roleDetailMap = details.stream().collect(Collectors.toMap(GridUserRoleDetail::getId, a -> a, (k1, k2) -> k1));
        GridUserRoleDetail detail = roleDetailMap.get(gridId);
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
        return false;
    }


    private void synchronizeSweepingData(SweepVillageVisitRecordingDO visitRecordingDO,String apiName) {
        Map<String,Object> map = new HashMap<>();
        map.put("id", ID_PREFIX + visitRecordingDO.getId());
        //删除操作
        if (apiName.equals(HuaweiSweepVillageUrlEnum.DELETE_SWEEPING_VILLAGE_DATA.getApiName())) {
            thirdApiMappingService.asyncDispatch(map, apiName,SmartGridContext.getMobile());
            return;
        }

        //添加接口 传入activityId
        if(apiName.equals(HuaweiSweepVillageUrlEnum.ADD_SWEEPING_VILLAGE_DATA.getApiName())){
            map.put("activityId", SweepVillageConstants.ID_PREFIX + visitRecordingDO.getActivityId());
        }
        map.put("tenantsId",visitRecordingDO.getTenantsId());
        map.put("successFlag",visitRecordingDO.getSuccessFlag());
        map.put("complaintFlag",visitRecordingDO.getComplaintSensitiveCustomersFlag());
        map.put("location",visitRecordingDO.getLocation());
        Gson gson = new Gson();
        List<StallUpBizType> businessType = gson.fromJson(visitRecordingDO.getBusinessType(), new TypeToken<List<StallUpBizType>>() {
        }.getType());
        List<SimpleStallUpBizType> simpleStallUpBizTypes = new ArrayList<>();
        if (!CollectionUtils.isEmpty(businessType)) {
            for (StallUpBizType stallUpBizType: businessType) {
                SimpleStallUpBizType simpleStallUpBizType = new SimpleStallUpBizType();
                simpleStallUpBizType.setId("" + stallUpBizType.getId());
                simpleStallUpBizType.setName(stallUpBizType.getName());
                simpleStallUpBizTypes.add(simpleStallUpBizType);
            }
        }
        map.put("bizTypes",simpleStallUpBizTypes);
        if (visitRecordingDO.getBroadbandExpireTime() != null) {
            map.put("broadbandExpireTime",visitRecordingDO.getBroadbandExpireTime().getTime());
        }
        if (visitRecordingDO.getTvBoxExpireTime() != null) {
            map.put("TVBoxExpireTime",visitRecordingDO.getTvBoxExpireTime().getTime());
        }
        map.put("remark",visitRecordingDO.getRemark());

        thirdApiMappingService.asyncDispatch(map, apiName,SmartGridContext.getMobile());
    }

}
