package com.shinemo.wangge.core.service.sweepstreet.impl;

import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.sweepstreet.domain.error.SweepStreetErrorCodes;
import com.shinemo.sweepstreet.domain.model.SweepStreetVisitRecordingDO;
import com.shinemo.sweepstreet.domain.query.SweepStreetVisitRecordingQuery;
import com.shinemo.sweepstreet.domain.vo.SweepStreetBusinessVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetVisitRecordingVO;
import com.shinemo.sweepvillage.domain.constant.SweepVillageConstants;
import com.shinemo.wangge.core.service.sweepstreet.VisitStreetService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.dal.mapper.SweepStreetVisitRecordingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ChenQinHai
 * @date 2020/8/13
 */
@Slf4j
@Service
public class VisitStreetServiceImpl implements VisitStreetService {

    @Resource
    private SweepStreetVisitRecordingMapper sweepStreetVisitRecordingMapper;

    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    @Override
    public ApiResult<Void> add(SweepStreetVisitRecordingVO request) {
        String mobile = SmartGridContext.getMobile();
        String userName = SmartGridContext.getUserName();

        SweepStreetVisitRecordingDO visitRecordingDO=new SweepStreetVisitRecordingDO();
        BeanUtils.copyProperties(request,visitRecordingDO);
        visitRecordingDO.setMobile(mobile);
        visitRecordingDO.setMarketingUserName(userName);
        sweepStreetVisitRecordingMapper.insert(visitRecordingDO);

        /*扫街录入数据同步华为*/
//        synchronizeSweepingData(visitRecordingDO,"");
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<List<SweepStreetVisitRecordingVO>> getVisitStreetByMerchantIdOrActivityId(SweepStreetVisitRecordingQuery query) {
        List<SweepStreetVisitRecordingDO>  doList=sweepStreetVisitRecordingMapper.find(query);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0,new ArrayList<>());
        }

        List<SweepStreetVisitRecordingVO> vos=new ArrayList<>(doList.size());
        for (SweepStreetVisitRecordingDO recordingDO:doList) {
            SweepStreetVisitRecordingVO recordingVO=new SweepStreetVisitRecordingVO();
            BeanUtils.copyProperties(recordingDO,recordingVO);
            vos.add(recordingVO);

        }
        return ApiResult.of(0,vos);
    }

    @Override
    public ApiResult<SweepStreetVisitRecordingVO> getVisitStreetDetail(Long id) {
        SweepStreetVisitRecordingQuery query =new SweepStreetVisitRecordingQuery();
        query.setId(id);
        query.setStatus(1);
        SweepStreetVisitRecordingDO visitRecordingDO=sweepStreetVisitRecordingMapper.get(query);
        if (null == visitRecordingDO){
            return  ApiResultWrapper.fail(SweepStreetErrorCodes.VISIT_RECORDING_NOT_EXIST);
        }

        SweepStreetVisitRecordingVO visitRecordingVO= new SweepStreetVisitRecordingVO();
        BeanUtils.copyProperties(visitRecordingDO,visitRecordingVO);
        return ApiResult.of(0,visitRecordingVO);
    }

    @Override
    public ApiResult<SweepStreetBusinessVO> getBusinessDetail(String merchantId) {
        SweepStreetVisitRecordingQuery query =new SweepStreetVisitRecordingQuery();
        query.setMerchantId(merchantId);
        query.setStatus(1);
        query.setOrderByEnable(true);
        query.putOrderBy("gmt_create",false);
        List<SweepStreetVisitRecordingDO>  doList=sweepStreetVisitRecordingMapper.find(query);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0,new SweepStreetBusinessVO());
        }
        SweepStreetBusinessVO sweepStreetBusinessVO= new SweepStreetBusinessVO();
        BeanUtils.copyProperties(doList.get(0),sweepStreetBusinessVO);
        return ApiResult.of(0,sweepStreetBusinessVO);
    }


    private void synchronizeSweepingData(SweepStreetVisitRecordingDO visitRecordingDO, String apiName) {

        Map<String,Object> map = new HashMap<>();
        map.put("id",visitRecordingDO.getId());

        map.put("activityId", SweepVillageConstants.ID_PREFIX + visitRecordingDO.getActivityId());
        map.put("merchantId",visitRecordingDO.getMerchantId());
        map.put("successFlag",visitRecordingDO.getSuccessFlag());
        map.put("complaintFlag",visitRecordingDO.getComplaintSensitiveCustomersFlag());
        map.put("location",visitRecordingDO.getLocation());
        map.put("bizType",visitRecordingDO.getBusinessType());
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
