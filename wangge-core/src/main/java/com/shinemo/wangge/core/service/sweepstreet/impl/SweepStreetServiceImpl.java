package com.shinemo.wangge.core.service.sweepstreet.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.shinemo.client.common.ListVO;
import com.shinemo.client.common.StatusEnum;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.sweepfloor.domain.vo.LocationDetailVO;
import com.shinemo.sweepstreet.domain.contants.SweepStreetActivityConstants;
import com.shinemo.sweepstreet.domain.huawei.HuaWeiCreateSweepStreetActivityRequest;
import com.shinemo.sweepstreet.domain.model.ParentSweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetActivityDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetMarketingNumberDO;
import com.shinemo.sweepstreet.domain.model.SweepStreetVisitRecordingDO;
import com.shinemo.sweepstreet.domain.query.ParentSweepStreetActivityQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetActivityQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetMarketingNumberQuery;
import com.shinemo.sweepstreet.domain.query.SweepStreetVisitRecordingQuery;
import com.shinemo.sweepstreet.domain.request.HuaweiMerchantRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetActivityRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetListRequest;
import com.shinemo.sweepstreet.domain.request.SweepStreetSignRequest;
import com.shinemo.sweepstreet.domain.response.HuaweiMerchantListResponse;
import com.shinemo.sweepstreet.domain.response.HuaweiMerchantResponse;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityFinishedVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetActivityVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMerchantListVO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetMerchantVO;
import com.shinemo.sweepstreet.enums.HuaweiSweepStreetActivityUrlEnum;
import com.shinemo.sweepstreet.enums.SweepStreetStatusEnum;
import com.shinemo.sweepstreet.error.SweepStreetErrorCodes;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetMarketService;
import com.shinemo.wangge.core.service.sweepstreet.SweepStreetService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.core.util.ValidatorUtil;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SweepStreetServiceImpl implements SweepStreetService {

    @Resource
    private SweepStreetActivityMapper sweepStreetActivityMapper;

    @Resource
    private SweepStreetMarketingNumberMapper sweepStreetMarketingNumberMapper;

    @Resource
    private SweepStreetMarketService sweepStreetMarketService;

    @Resource
    private SignRecordMapper signRecordMapper;

    @Resource
    private ParentSweepStreetActivityMapper parentSweepStreetActivityMapper;

    @Resource
    private ThirdApiMappingV2Service thirdApiMappingV2Service;

    @Resource
    private SweepStreetVisitRecordingMapper sweepStreetVisitRecordingMapper;


    private SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

    @NacosValue(value = "${groupSer.seed}", autoRefreshed = true)
    private String groupSeed;
    @NacosValue(value = "${huawei.groupSer.url}", autoRefreshed = true)
    private String groupSerUrl;

    @Override
    public ApiResult<ListVO<SweepStreetActivityVO>> getSweepStreetList(SweepStreetListRequest request) {
        SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
        streetActivityQuery.setStatus(request.getStatus());
        streetActivityQuery.setMobile(SmartGridContext.getMobile());
        if (request.getStartTime() != null) {
            streetActivityQuery.setEndFilterStartTime(new Date(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            streetActivityQuery.setEndFilterEndTime(new Date(request.getEndTime()));
        }
        if (request.getStatus().equals(SweepStreetStatusEnum.END.getId())) {
            streetActivityQuery.setPageEnable(true);
            List<Integer> statusList = new ArrayList<>();
            statusList.add(SweepStreetStatusEnum.ABNORMAL_END.getId());
            statusList.add(SweepStreetStatusEnum.AUTO_END.getId());
            statusList.add(SweepStreetStatusEnum.END.getId());
            streetActivityQuery.setStatusList(statusList);
            streetActivityQuery.setStatus(null);
            streetActivityQuery.setOrderByEnable(true);
            streetActivityQuery.putOrderBy("real_end_time",false);
        }

        if (request.getStatus().equals(SweepStreetStatusEnum.NOT_START.getId())) {
            streetActivityQuery.setOrderByEnable(true);
            streetActivityQuery.putOrderBy("plan_start_time",true);
        }

        List<SweepStreetActivityDO> activityDOS = sweepStreetActivityMapper.find(streetActivityQuery);
        if (CollectionUtils.isEmpty(activityDOS)) {
            return ApiResult.of(0, ListVO.list(new ArrayList<>(), 0));
        }
        //DO-->VO
        List<SweepStreetActivityVO> vos = new ArrayList<>(activityDOS.size());
        for (SweepStreetActivityDO activityDO : activityDOS) {
            SweepStreetActivityVO activityVO = new SweepStreetActivityVO();
            BeanUtils.copyProperties(activityDO, activityVO);
            activityVO.setAddress(activityDO.getSignAddress());
            vos.add(activityVO);
        }
        if (!request.getStatus().equals(SweepStreetStatusEnum.END.getId())) {
            return ApiResult.of(0, ListVO.list(vos, vos.size()));
        }
        long count = sweepStreetActivityMapper.count(streetActivityQuery);
        //查询办理量
        List<Long> activityIds = vos.stream().map(SweepStreetActivityVO::getId).collect(Collectors.toList());
        SweepStreetMarketingNumberQuery numberQuery = new SweepStreetMarketingNumberQuery();
        numberQuery.setActivityIds(activityIds);
        List<SweepStreetMarketingNumberDO> numberDOS = sweepStreetMarketingNumberMapper.find(numberQuery);

        //查询走访商户数量
        SweepStreetVisitRecordingQuery recordingQuery = new SweepStreetVisitRecordingQuery();
        recordingQuery.setActivityIds(activityIds);
        List<SweepStreetVisitRecordingDO> recordingDOS = sweepStreetVisitRecordingMapper.find(recordingQuery);
        Map<Long, List<SweepStreetVisitRecordingDO>> listMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(recordingDOS)) {
            listMap = recordingDOS.stream().collect(Collectors.groupingBy(SweepStreetVisitRecordingDO::getActivityId));
        }
        Map<Long, List<SweepStreetMarketingNumberDO>> map = numberDOS.stream().collect(Collectors.groupingBy(SweepStreetMarketingNumberDO::getSweepStreetId));
        for (SweepStreetActivityVO activityVO : vos) {
            List<SweepStreetMarketingNumberDO> marketingNumberDOS = map.get(activityVO.getId());
            if (CollectionUtils.isEmpty(marketingNumberDOS)) {
                activityVO.setBusinessCount(0);
            } else {
                activityVO.setBusinessCount(marketingNumberDOS.get(0).getCount());
            }
            if (!CollectionUtils.isEmpty(listMap)) {
                List<SweepStreetVisitRecordingDO> visitRecordingDOS = listMap.get(activityVO.getId());
                if (!CollectionUtils.isEmpty(visitRecordingDOS)) {
                    activityVO.setVisitCount(visitRecordingDOS.size());
                }
            }
        }

        return ApiResult.of(0, ListVO.list(vos, count));
    }


    @Override
    public ApiResult startSign(SweepStreetSignRequest request) {
        SweepStreetActivityDO sweepStreetActivityDO = getDOById(request.getActivityId());
        if (sweepStreetActivityDO == null) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.ACTIVITY_NOT_EXIT);
        }

        if (!SmartGridContext.getMobile().equals(sweepStreetActivityDO.getMobile())) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.AUTH_ERROR);
        }

        //校验当前活动状态
        if (SweepStreetStatusEnum.NOT_START.getId() != sweepStreetActivityDO.getStatus()) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.ACTIVITY_START_ERROR);
        }

        //校验是否存在进行中活动
        ApiResult checkResult = checkStatusWhenStart();
        if (checkResult != null) {
            return checkResult;
        }

        //插入签到表
        SignRecordDO signRecordDO = new SignRecordDO();
        signRecordDO.setUserName(SmartGridContext.getUserName());
        signRecordDO.setMobile(SmartGridContext.getMobile());
        signRecordDO.setActivityId(sweepStreetActivityDO.getId());
        signRecordDO.setUserId(SmartGridContext.getUid());
        signRecordDO.setBizType(SignRecordBizTypeEnum.SWEEP_STREET.getId());
        signRecordDO.setStartLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        signRecordDO.setStartTime(new Date());
        signRecordMapper.insert(signRecordDO);
        //更新子活动状态
        Date startTime = new Date();
        SweepStreetActivityDO updateActivityDO = new SweepStreetActivityDO();
        updateActivityDO.setId(sweepStreetActivityDO.getId());
        updateActivityDO.setStatus(SweepStreetStatusEnum.PROCESSING.getId());
        updateActivityDO.setRealStartTime(startTime);
        updateActivityDO.setSignAddress(request.getLocationDetailVO().getAddress());
        updateActivityDO.setSignLocation(request.getLocationDetailVO().getLocation());
        sweepStreetActivityMapper.update(updateActivityDO);
        //更新父活动状态
        updateParentStatus(sweepStreetActivityDO, SweepStreetStatusEnum.PROCESSING.getId(), startTime);

        //同步华为
        startSignSyncHuaWei(request, updateActivityDO);

        return ApiResult.of(0);
    }

    private void startSignSyncHuaWei(SweepStreetSignRequest request, SweepStreetActivityDO updateActivityDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + updateActivityDO.getId());
        map.put("parentActivityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + updateActivityDO.getParentId());
        map.put("status", SweepStreetStatusEnum.PROCESSING.getId());
        String location = request.getLocationDetailVO().getLocation();
        String[] locations = StrUtil.split(location, ",");
        map.put("startLongitude", locations[0]);
        map.put("startLatitude", locations[1]);
        map.put("startAddress", request.getLocationDetailVO().getAddress());
        map.put("startTime", DateUtil.formatDateTime(updateActivityDO.getRealStartTime()));
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.UPDATE_SWEEP_STREET_ACTIVITY.getApiName(), SmartGridContext.getMobile());
    }

    @Override
    public ApiResult endSign(SweepStreetSignRequest request) {
        SweepStreetActivityDO sweepStreetActivityDO = getDOById(request.getActivityId());
        if (sweepStreetActivityDO == null) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.ACTIVITY_NOT_EXIT);
        }

        //校验当前活动状态，非进行中状态不可签离
        if (SweepStreetStatusEnum.PROCESSING.getId() != sweepStreetActivityDO.getStatus()) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.ACTIVITY_END_ERROR);
        }
        /*更新子活动表的个人活动数据*/
        SignRecordQuery query = new SignRecordQuery();
        query.setActivityId(request.getActivityId());
        query.setUserId(SmartGridContext.getUid());
        SignRecordDO signRecordDO = signRecordMapper.get(query);

        if (signRecordDO == null) {
            log.error("[endSign] signRecordDO is null,activityId = {}", sweepStreetActivityDO.getId());
            return ApiResultWrapper.fail(SweepStreetErrorCodes.ACTIVITY_SEARCH_ERROR);
        }

        SignRecordDO updateSignDO = new SignRecordDO();
        updateSignDO.setId(signRecordDO.getId());
        updateSignDO.setRemark(request.getRemark());
        updateSignDO.setEndTime(new Date());
        updateSignDO.setEndLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        updateSignDO.setImgUrl(GsonUtils.toJson(request.getPicUrls()));
        signRecordMapper.update(updateSignDO);

        /*更新子表活动记录*/
        Date endTime = new Date();
        SweepStreetActivityDO updateStreetActivityDO = new SweepStreetActivityDO();
        updateStreetActivityDO.setId(sweepStreetActivityDO.getId());
        updateStreetActivityDO.setStatus(SweepStreetStatusEnum.END.getId());
        updateStreetActivityDO.setRealEndTime(endTime);
        sweepStreetActivityMapper.update(updateStreetActivityDO);
        //更新父活动
        updateParentStatus(sweepStreetActivityDO, SweepStreetStatusEnum.END.getId(), endTime);

        //同步华为
        endSignSyncHuaWei(request, updateStreetActivityDO);

        return ApiResult.of(0);
    }

    private void endSignSyncHuaWei(SweepStreetSignRequest request, SweepStreetActivityDO updateStreetActivityDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + updateStreetActivityDO.getId());
        map.put("parentActivityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + updateStreetActivityDO.getParentId());
        map.put("status", SweepStreetStatusEnum.END.getId());
        String[] split = StrUtil.split(request.getLocationDetailVO().getLocation(), ",");
        map.put("endLongitude", split[0]);
        map.put("endLatitude", split[1]);
        map.put("endAddress", request.getLocationDetailVO().getAddress());
        map.put("endTime", DateUtil.formatDateTime(updateStreetActivityDO.getRealEndTime()));
        map.put("remark", request.getRemark());
        map.put("picUrl", CollUtil.join(request.getPicUrls(), ","));
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.UPDATE_SWEEP_STREET_ACTIVITY.getApiName(), SmartGridContext.getMobile());
    }

    @Override
    public ApiResult<Void> autoEnd(SweepStreetActivityDO streetActivityDO) {
        SignRecordQuery query = new SignRecordQuery();
        query.setActivityId(streetActivityDO.getId());
        query.setUserId(SmartGridContext.getUid());
        SignRecordDO signRecordDO = signRecordMapper.get(query);

        int status = SweepStreetStatusEnum.AUTO_END.getId();
        Date endTime = new Date();

        //更新签到表
        if (signRecordDO == null) {
            signRecordDO = new SignRecordDO();
            signRecordDO.setEndTime(endTime);
            signRecordDO.setBizType(SignRecordBizTypeEnum.SWEEP_STREET.getId());
            signRecordDO.setUserId(streetActivityDO.getCreatorId().toString());
            signRecordDO.setActivityId(streetActivityDO.getId());
            signRecordDO.setMobile(streetActivityDO.getMobile());
            signRecordMapper.insert(signRecordDO);
        } else {
            SignRecordDO newSignRecordDO = new SignRecordDO();
            newSignRecordDO.setId(signRecordDO.getId());
            newSignRecordDO.setEndTime(endTime);
            signRecordMapper.update(newSignRecordDO);
        }

        //更新子活动表
        streetActivityDO.setStatus(status);
        streetActivityDO.setRealEndTime(endTime);
        sweepStreetActivityMapper.update(streetActivityDO);
        //更新父活动表
        updateParentStatus(streetActivityDO, status, endTime);

        //同步华为
        autoEndSyncHuaWei(streetActivityDO);

        return ApiResult.of(0);
    }

    private void autoEndSyncHuaWei(SweepStreetActivityDO streetActivityDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + streetActivityDO.getId());
        map.put("parentActivityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + streetActivityDO.getParentId());
        map.put("status", SweepStreetStatusEnum.AUTO_END.getId());
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.UPDATE_SWEEP_STREET_ACTIVITY.getApiName(), streetActivityDO.getMobile());
    }

    @Override
    public ApiResult<SweepStreetActivityFinishedVO> getFinishedCount(Integer type) {
        SweepStreetActivityFinishedVO result = new SweepStreetActivityFinishedVO();

        //默认查询本周
        Date startTime = DateUtils.getThisWeekMonday();
        String mobile = SmartGridContext.getMobile();
        if (type == 2) {
            startTime = DateUtils.getThisMonthFirstDay();
        }
        //查询已结束活动:实际结束时间 >= startTime && 实际结束时间 <= endTime
        SweepStreetActivityQuery query = new SweepStreetActivityQuery();
        query.setMobile(mobile);
        query.setEndFilterStartTime(startTime);
        query.setEndFilterEndTime(new Date());
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SweepStreetStatusEnum.END.getId());
        statusList.add(SweepStreetStatusEnum.ABNORMAL_END.getId());
        statusList.add(SweepStreetStatusEnum.AUTO_END.getId());
        query.setStatusList(statusList);
        List<SweepStreetActivityDO> sweepStreetActivityDOS = sweepStreetActivityMapper.find(query);

        SweepStreetVisitRecordingQuery recordingQuery = new SweepStreetVisitRecordingQuery();
        recordingQuery.setFilterCreateTime(true);
        recordingQuery.setStartTime(startTime);
        recordingQuery.setEndTime(new Date());
        recordingQuery.setMobile(SmartGridContext.getMobile());
        recordingQuery.setStatus(StatusEnum.NORMAL.getId());
        List<SweepStreetVisitRecordingDO> recordingDOS = sweepStreetVisitRecordingMapper.find(recordingQuery);

        result.setActivityCount(sweepStreetActivityDOS.size());
        result.setBusinessCount(recordingDOS.size());
        return ApiResult.of(0, result);
    }

    @Transactional
    @Override
    public ApiResult<Void> createSweepStreet(SweepStreetActivityRequest sweepStreetActivityRequest) {
        ValidatorUtil.validateEntity(sweepStreetActivityRequest);

        //创建父活动
        ParentSweepStreetActivityDO parentSweepStreetActivityDO = getParentSweepStreetActivityDO(sweepStreetActivityRequest);
        parentSweepStreetActivityMapper.insert(parentSweepStreetActivityDO);

        List<SweepStreetActivityRequest.PartnerBean> partnerList = sweepStreetActivityRequest.getPartner();
        List<SweepStreetActivityDO> childActivityList = new ArrayList<>();
        for (SweepStreetActivityRequest.PartnerBean partnerBean : partnerList) {
            //创建子活动
            SweepStreetActivityDO sweepStreetActivityDO = getSweepStreetActivityDO(parentSweepStreetActivityDO, partnerBean);
            sweepStreetActivityMapper.insert(sweepStreetActivityDO);
            //创建业务办理 不同步华为
            sweepStreetMarketService.enterDefaultMarketingNumber(sweepStreetActivityDO.getId());
            childActivityList.add(sweepStreetActivityDO);
        }

        //同步华为
        syncCreateSweepStreetActivity(sweepStreetActivityRequest, parentSweepStreetActivityDO, childActivityList);
        log.info("[createSweepStreet] 新建扫街活动成功, 父活动id:{},创建人mobile:{}", parentSweepStreetActivityDO.getId(), parentSweepStreetActivityDO.getMobile());


        return ApiResult.of(0);
    }

    @Override
    public ApiResult<SweepStreetMerchantListVO> getMerchantList(HuaweiMerchantRequest request) {
        //透传华为
        String[] split = request.getLocation().split(",");
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupName", request.getQueryParam());
        map.put("contractPhone", request.getQueryParam());
        map.put("contractName", request.getQueryParam());
        map.put("longitude", split[0]);
        map.put("latitude", split[1]);
        map.put("pageSize", request.getPageSize());
        map.put("pageNum", request.getCurrentPage());
        map.put("radius", SweepStreetActivityConstants.DEFAULT_RADIUS);


        ApiResult<Map<String, Object>> result = thirdApiMappingV2Service.dispatch(map, HuaweiSweepStreetActivityUrlEnum.FIND_MERCHANT_LIST.getApiName());

        //华为response -> 前端VO
        HuaweiMerchantListResponse merchantListResponse = BeanUtil.mapToBean(result.getData(), HuaweiMerchantListResponse.class, false);
        List<SweepStreetMerchantVO> merchantVOList = new ArrayList<>();
        for (HuaweiMerchantResponse response : merchantListResponse.getGroupList()) {
            Date broadbandExpireTime = null;
            Date visitTime = null;
            try {
                if(response.getBroadbandExpireTime() != null){
                    broadbandExpireTime = format.parse(response.getBroadbandExpireTime());
                }
                if(response.getVisitTime() != null){
                    visitTime = format.parse(response.getVisitTime());
                }
            } catch (ParseException e) {
                log.error("[getMerchantList] ParseException e:{}", e);
                return ApiResult.fail(500, e.getMessage());
            }
            merchantVOList.add(SweepStreetMerchantVO.builder()
                    .merchantId(response.getMerchantsId())
                    .merchantName(response.getGroupName())
                    .merchantAddress(response.getGroupAddress())
                    .creatorMobile(response.getCreatorMobile())
                    .contactName(response.getContactPerson())
                    .contactMobile(response.getContactMobile())
                    .hasBroadband(response.getHasBroadband())
                    .broadbandExpireTime(broadbandExpireTime == null ? null:broadbandExpireTime.getTime())
                    .location(response.getLocation())
                    .visitTime(visitTime == null ? null:visitTime.getTime())
                    .distance(response.getDistance())
                    .build());
        }
        SweepStreetMerchantListVO sweepStreetMerchantListVO = new SweepStreetMerchantListVO();
        sweepStreetMerchantListVO.setMerchantsList(merchantVOList);
        sweepStreetMerchantListVO.setTotalSize(merchantListResponse.getTotalSize());
        return ApiResult.of(0, sweepStreetMerchantListVO);
    }

    @Override
    public ApiResult cancel(Long id) {
        SweepStreetActivityDO streetActivityDO = getDOById(id);
        if (streetActivityDO == null) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.ACTIVITY_NOT_EXIT);
        }
        if (!SmartGridContext.getMobile().equals(streetActivityDO.getMobile())) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.AUTH_ERROR);
        }
        //更新子活动
        Date time = new Date();
        SweepStreetActivityDO upActivityDO = new SweepStreetActivityDO();
        upActivityDO.setId(streetActivityDO.getId());
        upActivityDO.setStatus(SweepStreetStatusEnum.CANCEL.getId());
        sweepStreetActivityMapper.update(upActivityDO);
        //更新父活动
        updateParentStatus(streetActivityDO, SweepStreetStatusEnum.CANCEL.getId(), time);

        //同步华为
        cancelSyncHuaWei(upActivityDO);

        return ApiResult.of(0);
    }

    @Override
    public ApiResult<String> getBusinessDetailUrl(String merchantId,String location) {
        long timestamp = System.currentTimeMillis();

        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", SmartGridContext.getMobile());
        formData.put("groupid",merchantId);
        formData.put("timestamp",timestamp);
        formData.put("menuid","groupinfo");
        String[] split = StrUtil.split(location, ",");
        formData.put("longitude",split[0]);
        formData.put("latitude",split[1]);

        log.info("[getBusinessDetailUrl] 请求参数 formData:{}", formData);
        String paramStr = EncryptUtil.buildParameterString(formData);

        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, groupSeed);

        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData + "," + groupSeed + "," + timestamp);

        String url = groupSerUrl + "?";

        StringBuilder sb = new StringBuilder(url);
        sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign);

        String businessDetailUrl = sb.toString();
        log.info("[getBusinessDetailUrl]商户id:{},生成商户详情url:{}", merchantId, businessDetailUrl);

        return ApiResult.of(0, businessDetailUrl);


    }

    private void cancelSyncHuaWei(SweepStreetActivityDO upActivityDO) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + upActivityDO.getId());
        map.put("parentActivityId", SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + upActivityDO.getParentId());
        map.put("status", SweepStreetStatusEnum.CANCEL.getId());
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.UPDATE_SWEEP_STREET_ACTIVITY.getApiName(), SmartGridContext.getMobile());
    }

    private SweepStreetActivityDO getDOById(Long id) {
        SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
        streetActivityQuery.setId(id);
        return sweepStreetActivityMapper.get(streetActivityQuery);
    }

    /**
     * 校验是否有进行中活动
     *
     * @return
     */
    private ApiResult checkStatusWhenStart() {

        SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
        streetActivityQuery.setMobile(SmartGridContext.getMobile());
        streetActivityQuery.setStatus(SweepStreetStatusEnum.PROCESSING.getId());
        List<SweepStreetActivityDO> streetActivityDOS = sweepStreetActivityMapper.find(streetActivityQuery);
        if (!CollectionUtils.isEmpty(streetActivityDOS)) {
            return ApiResultWrapper.fail(SweepStreetErrorCodes.STARTED_ACTIVITY_EXIT);
        }
        return null;
    }


    /**
     * 更新父活动状态
     *
     * @param status
     */
    private void updateParentStatus(SweepStreetActivityDO sweepStreetActivityDO, Integer status, Date time) {

        ParentSweepStreetActivityDO parentActivityDo = new ParentSweepStreetActivityDO();
        //子活动有一个开始，父活动即为开始
        if (status == SweepStreetStatusEnum.PROCESSING.getId()) {
            //判断父活动是否已开始
            ParentSweepStreetActivityDO parentSweepStreetActivityDO = getParentActivityById(sweepStreetActivityDO.getParentId());
            if (parentSweepStreetActivityDO.getStatus().equals(SweepStreetStatusEnum.NOT_START.getId())) {
                parentActivityDo.setId(sweepStreetActivityDO.getParentId());
                parentActivityDo.setStatus(SweepStreetStatusEnum.PROCESSING.getId());
                parentActivityDo.setRealStartTime(time);
            }
        } else if (status == SweepStreetStatusEnum.END.getId() ||
                status == SweepStreetStatusEnum.ABNORMAL_END.getId()
                || status == SweepStreetStatusEnum.CANCEL.getId()
                || status == SweepStreetStatusEnum.AUTO_END.getId()) {
            //所有子活动结束，父活动即为结束
            SweepStreetActivityQuery streetActivityQuery = new SweepStreetActivityQuery();
            streetActivityQuery.setParentId(sweepStreetActivityDO.getParentId());
            List<SweepStreetActivityDO> streetActivityDOS = sweepStreetActivityMapper.find(streetActivityQuery);
            List<SweepStreetActivityDO> notEndList = streetActivityDOS.stream().
                    filter(a -> a.getStatus().equals(SweepStreetStatusEnum.NOT_START.getId()) ||
                            a.getStatus().equals(SweepStreetStatusEnum.PROCESSING.getId())).
                    collect(Collectors.toList());
            if (CollectionUtils.isEmpty(notEndList)) {
                //更新父活动状态未已结束
                parentActivityDo.setStatus(SweepStreetStatusEnum.END.getId());
                parentActivityDo.setId(sweepStreetActivityDO.getParentId());
                parentActivityDo.setRealEndTime(time);
            }
        }

        parentSweepStreetActivityMapper.update(parentActivityDo);
    }

    private ParentSweepStreetActivityDO getParentActivityById(Long id) {
        ParentSweepStreetActivityQuery parentSweepStreetActivityQuery = new ParentSweepStreetActivityQuery();
        parentSweepStreetActivityQuery.setId(id);
        ParentSweepStreetActivityDO parentActivity = parentSweepStreetActivityMapper.get(parentSweepStreetActivityQuery);
        return parentActivity;
    }

    /**
     * 拼装活动打卡位置信息
     *
     * @param vos
     */
    private void buildLocation(List<SweepStreetActivityVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        List<Long> activityIds = vos.stream().map(SweepStreetActivityVO::getId).collect(Collectors.toList());
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setActivityIds(activityIds);
        signRecordQuery.setBizType(SignRecordBizTypeEnum.SWEEP_STREET.getId());
        List<SignRecordDO> signRecordDOS = signRecordMapper.find(signRecordQuery);
        if (CollectionUtils.isEmpty(signRecordDOS)) {
            return;
        }
        Map<Long, List<SignRecordDO>> listMap = signRecordDOS.stream().collect(Collectors.groupingBy(SignRecordDO::getActivityId));
        for (SweepStreetActivityVO activityVO : vos) {
            List<SignRecordDO> recordDOS = listMap.get(activityVO.getId());
            if (!CollectionUtils.isEmpty(recordDOS)) {
                LocationDetailVO locationDetailVO = GsonUtils.fromGson2Obj(recordDOS.get(0).getStartLocation(), LocationDetailVO.class);
                activityVO.setLocation(locationDetailVO.getLocation());
                activityVO.setAddress(locationDetailVO.getAddress());
            }
        }
    }


    private void syncCreateSweepStreetActivity(SweepStreetActivityRequest request, ParentSweepStreetActivityDO parentSweepStreetActivityDO, List<SweepStreetActivityDO> childActivityList) {
        HuaWeiCreateSweepStreetActivityRequest huaWeiRequest = new HuaWeiCreateSweepStreetActivityRequest();
        huaWeiRequest.setParentActivityId(SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + parentSweepStreetActivityDO.getId());
        huaWeiRequest.setTitle(request.getTitle());
        huaWeiRequest.setStartTime(DateUtil.formatDateTime(parentSweepStreetActivityDO.getPlanStartTime()));
        huaWeiRequest.setEndTime(DateUtil.formatDateTime(parentSweepStreetActivityDO.getPlanEndTime()));
        huaWeiRequest.setStatus(String.valueOf(parentSweepStreetActivityDO.getStatus()));

        List<HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity> childSweepStreetActivityArrayList = new ArrayList<>();

        for (SweepStreetActivityDO sweepStreetActivityDO : childActivityList) {
            HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity childSweepStreetActivity = new HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity();
            childSweepStreetActivity.setActivityId(SweepStreetActivityConstants.SJ_ACTIVITYID_PREFIX + sweepStreetActivityDO.getId());
            List<HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity.Participant> participantList = new ArrayList<>();
            List<SweepStreetActivityRequest.PartnerBean> partnerBeanList = GsonUtils.fromJsonToList(sweepStreetActivityDO.getPartner(), SweepStreetActivityRequest.PartnerBean[].class);
            for (SweepStreetActivityRequest.PartnerBean partnerBean : partnerBeanList) {
                HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity.Participant participant = new HuaWeiCreateSweepStreetActivityRequest.ChildSweepStreetActivity.Participant();
                //判断用户来源
                if (StrUtil.isNotBlank(partnerBean.getUserId())) {
                    //来自网格
                    participant.setUserSource("1");
                    participant.setUserId(partnerBean.getUserId());
                    participant.setUserPhone(partnerBean.getMobile());
                } else {
                    //来自通讯录
                    participant.setUserSource("2");
                    participant.setUserName(partnerBean.getName());
                    participant.setUserPhone(partnerBean.getMobile());
                }

                //判断参与人类型
                if (partnerBean.getMobile().equals(sweepStreetActivityDO.getMobile())) {
                    //负责人
                    participant.setUserType("1");
                } else {
                    //参与人
                    participant.setUserType("2");
                }

                participantList.add(participant);
            }
            childSweepStreetActivity.setParticipantList(participantList);
            childSweepStreetActivityArrayList.add(childSweepStreetActivity);
        }

        huaWeiRequest.setChildrenList(childSweepStreetActivityArrayList);
        Map<String, Object> map = BeanUtil.beanToMap(huaWeiRequest, false, true);
        thirdApiMappingV2Service.asyncDispatch(map, HuaweiSweepStreetActivityUrlEnum.CREATE_SWEEP_STREET_ACTIVITY.getApiName(), SmartGridContext.getMobile());
    }


    private SweepStreetActivityDO getSweepStreetActivityDO(ParentSweepStreetActivityDO parentSweepStreetActivityDO, SweepStreetActivityRequest.PartnerBean partnerBean) {
        SweepStreetActivityDO sweepStreetActivityDO = new SweepStreetActivityDO();
        sweepStreetActivityDO.setParentId(parentSweepStreetActivityDO.getId());
        sweepStreetActivityDO.setTitle(parentSweepStreetActivityDO.getTitle());
        sweepStreetActivityDO.setCreatorId(parentSweepStreetActivityDO.getCreatorId());
        sweepStreetActivityDO.setCreatorName(parentSweepStreetActivityDO.getCreatorName());
        sweepStreetActivityDO.setPlanStartTime(parentSweepStreetActivityDO.getPlanStartTime());
        sweepStreetActivityDO.setPlanEndTime(parentSweepStreetActivityDO.getPlanEndTime());
        sweepStreetActivityDO.setLocation(parentSweepStreetActivityDO.getLocation());
        sweepStreetActivityDO.setPartner(GsonUtils.toJson(parentSweepStreetActivityDO.getPartner()));
        sweepStreetActivityDO.setStatus(SweepStreetStatusEnum.NOT_START.getId());
        sweepStreetActivityDO.setMobile(partnerBean.getMobile());
        sweepStreetActivityDO.setName(partnerBean.getName());
        sweepStreetActivityDO.setCurrentPartnerDetail(GsonUtils.toJson(partnerBean));
        return sweepStreetActivityDO;
    }


    private ParentSweepStreetActivityDO getParentSweepStreetActivityDO(SweepStreetActivityRequest request) {
        ParentSweepStreetActivityDO parentSweepStreetActivityDO = new ParentSweepStreetActivityDO();
        parentSweepStreetActivityDO.setTitle(request.getTitle());
        if (StrUtil.isNotBlank(SmartGridContext.getUid())) {
            parentSweepStreetActivityDO.setCreatorId(Long.valueOf(SmartGridContext.getUid()));
        }
        if (StrUtil.isNotBlank(SmartGridContext.getOrgId())) {
            parentSweepStreetActivityDO.setCreatorOrgId(Long.valueOf(SmartGridContext.getOrgId()));
        }

        parentSweepStreetActivityDO.setCreatorName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        parentSweepStreetActivityDO.setMobile(SmartGridContext.getMobile());
        parentSweepStreetActivityDO.setPlanStartTime(new Date(request.getPlanStartTime()));
        parentSweepStreetActivityDO.setPlanEndTime(new Date(request.getPlanEndTime()));
        parentSweepStreetActivityDO.setLocation(request.getLocation());
        parentSweepStreetActivityDO.setPartner(GsonUtils.toJson(request.getPartner()));
        parentSweepStreetActivityDO.setStatus(SweepStreetStatusEnum.NOT_START.getId());
        try {
            parentSweepStreetActivityDO.setGridId(SmartGridContext.getSelectGridUserRoleDetail().getId());
        } catch (Exception e) {
            log.error("[createSweepStreetActivity] 该用户无网格,mobile:{}", SmartGridContext.getMobile());
            parentSweepStreetActivityDO.setGridId("0");
        }
        return parentSweepStreetActivityDO;
    }

}
