package com.shinemo.wangge.core.service.sweepfloor.impl;

import com.shinemo.client.common.ListVO;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.AESUtil;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.model.SweepFloorBizDetail;
import com.shinemo.stallup.domain.request.HuaWeiRequest;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepfloor.common.enums.CommonFlagEnum;
import com.shinemo.sweepfloor.common.enums.HuaweiSweepFloorUrlEnum;
import com.shinemo.sweepfloor.common.enums.SignRecordBizTypeEnum;
import com.shinemo.sweepfloor.common.enums.SweepFloorStatusEnum;
import com.shinemo.sweepfloor.common.error.SweepFloorErrorCodes;
import com.shinemo.sweepfloor.domain.model.*;
import com.shinemo.sweepfloor.domain.query.*;
import com.shinemo.sweepfloor.domain.request.HuaweiBuildingDetailListRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiBuildingRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiCellAndBuildingsRequest;
import com.shinemo.sweepfloor.domain.request.HuaweiTenantRequest;
import com.shinemo.sweepfloor.domain.response.*;
import com.shinemo.sweepfloor.domain.vo.*;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.sweepfloor.SweepFloorService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.core.util.HuaWeiUtil;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SweepFloorServiceImpl implements SweepFloorService {

    private static final Integer WEEK_TYPE = 1;
    private static final Integer MONTH_TYPE = 2;

    private static final Integer SUCCESS_FLAG = 1;

    @Resource
    private SweepFloorActivityMapper sweepFloorActivityMapper;
    @Resource
    private SweepFloorVisitRecordingMapper sweepFloorVisitRecordingMapper;
    @Resource
    private SweepFloorMarketingNumberMapper sweepFloorMarketingNumberMapper;
    @Resource
    private SignRecordMapper signRecordMapper;
    @Resource
    private HuaWeiService huaWeiService;
    @Resource
    private BusinessConfigMapper businessConfigMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private StallUpConfig stallUpConfig;
    @Resource
    private SmartGridActivityMapper smartGridActivityMapper;
    @Resource
    private ThirdApiMappingService thirdApiMappingService;

    @Value("${smartgrid.huawei.aesKey}")
    public String aeskey;

    private static final List<Character> numList = new ArrayList<Character>() {{
        add('0');
        add('1');
        add('2');
        add('3');
        add('4');
        add('5');
        add('6');
        add('7');
        add('8');
        add('9');
    }};

    private static final List<Character> stringList = new ArrayList<Character>() {{
        add('a');
        add('b');
        add('c');
        add('d');
        add('e');
        add('f');
        add('g');
        add('h');
        add('i');
        add('j');
        add('k');
        add('l');
        add('m');
        add('n');
        add('o');
        add('p');
        add('q');
        add('r');
        add('s');
        add('t');
        add('u');
        add('v');
        add('w');
        add('x');
        add('y');
        add('z');
        add('A');
        add('B');
        add('C');
        add('D');
        add('E');
        add('F');
        add('G');
        add('H');
        add('I');
        add('J');
        add('K');
        add('L');
        add('M');
        add('N');
        add('O');
        add('P');
        add('Q');
        add('R');
        add('S');
        add('T');
        add('U');
        add('V');
        add('W');
        add('X');
        add('Y');
        add('Z');
    }};

    private static final String ID_PREFIX = "SL_";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Long> create(SweepFloorActivityVO request) {

        SweepFloorActivityDO activityDO = new SweepFloorActivityDO();
        activityDO.setAddress(request.getAddress());
        activityDO.setCommunityId(request.getCommunityId());
        activityDO.setCommunityName(request.getCommunityName());
        activityDO.setCreator(SmartGridContext.getUid());
        activityDO.setCreatorName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        activityDO.setCreatorOrgId(SmartGridContext.getOrgId());
        activityDO.setLocation(request.getLocation());
        activityDO.setMobile(SmartGridContext.getMobile());
        activityDO.setGmtCreate(new Date());
        activityDO.setGmtModified(new Date());

        String selectGridInfo = SmartGridContext.getSelectGridInfo();
        GridUserRoleDetail gridDetail = GsonUtils.fromGson2Obj(selectGridInfo, GridUserRoleDetail.class);
        activityDO.setGridId(gridDetail.getId());
        sweepFloorActivityMapper.insert(activityDO);

        SmartGridActivityDO smartGridActivityDO = new SmartGridActivityDO();
        smartGridActivityDO.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
        smartGridActivityDO.setActivityId(activityDO.getId());
        smartGridActivityDO.setGridId(gridDetail.getId());
        smartGridActivityDO.setGridName(gridDetail.getName());
        smartGridActivityMapper.insert(smartGridActivityDO);

        synchronizeSweepingData(activityDO);

        return ApiResult.of(0, activityDO.getId());
    }

    private void synchronizeSweepingData(SweepFloorActivityDO activityDO) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",ID_PREFIX + activityDO.getId());
        map.put("communityName",activityDO.getCommunityName());
        map.put("communityId",activityDO.getCommunityId());
        map.put("location",activityDO.getLocation());
        map.put("address",activityDO.getAddress());
        map.put("creatorName",activityDO.getCreatorName());
        map.put("mobile",activityDO.getMobile());
        map.put("gmtCreate",DateUtils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
        map.put("status",SweepFloorStatusEnum.NOT_START.getId());
        thirdApiMappingService.asyncDispatch(map, HuaweiSweepFloorUrlEnum.SYNCHRONIZE_SWEEPING_DATA.getMethod(),activityDO.getMobile());
    }

    @Override
    public ApiResult<Void> updateSweepFloorStatus(UpdateSweepFloorStatusQuery request) {
        SweepFloorActivityQuery query = new SweepFloorActivityQuery();
        query.setStatus(request.getStatus());
        query.setId(request.getId());
        SweepFloorActivityDO dbActivity = sweepFloorActivityMapper.get(query);
        if (dbActivity == null) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_ACTIVITY_NOT_EXIST);
        } else if (dbActivity.getStatus() != SweepFloorStatusEnum.NOT_START.getId()) {
            log.error("updateSweepFloorStatus failed,dbStatus={}", dbActivity.getStatus());
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_STATUS_ERROR);
        }
        SweepFloorActivityDO activityDO = new SweepFloorActivityDO();
        activityDO.setId(request.getId());
        activityDO.setStatus(request.getStatus());
        activityDO.setEndTime(new Date());
        sweepFloorActivityMapper.update(activityDO);
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<SweepFloorBusinessCountAndHouseCountVO> getBusinessCountAndHouseCount(Integer type) {
        SweepFloorVisitRecordingQuery query = new SweepFloorVisitRecordingQuery();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        query.setMarketingUserId(SmartGridContext.getUid());
        if (WEEK_TYPE.equals(type)) {
            query.setStartTime(DateUtils.getThisWeekMonday(zero));
        } else if (MONTH_TYPE.equals(type)) {
            query.setStartTime(DateUtils.getThisMonthFirstDay());
        }
        query.setEndTime(new Date());
        log.info("[getBusinessCountAndHouseCount] query = {}", query);
        List<SweepFloorVisitRecordingDO> recordingDOS = sweepFloorVisitRecordingMapper.find(query);
        SweepFloorBusinessCountAndHouseCountVO resultVO = new SweepFloorBusinessCountAndHouseCountVO();
        if (CollectionUtils.isEmpty(recordingDOS)) {
            resultVO.setBusinessCount(0);
            resultVO.setHouseCount(0);
            return ApiResult.of(0, resultVO);
        }
        Map<String, List<SweepFloorVisitRecordingDO>> map = recordingDOS.stream()
                .collect(Collectors.groupingBy(SweepFloorVisitRecordingDO::getHouseId));

        int count = 0;
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            List<SweepFloorVisitRecordingDO> sweepFloorVisitRecordingDOS = map.get(next);

            for (SweepFloorVisitRecordingDO recordingDO : sweepFloorVisitRecordingDOS) {
                if (recordingDO.getSuccessFlag().equals(SUCCESS_FLAG)) {
                    count++;
                    break;
                }
            }
        }
        log.info("[getBusinessCountAndHouseCount] houseCount = {},businessCount = {}", map.size(), count);
        resultVO.setHouseCount(map.size());
        resultVO.setBusinessCount(count);
        return ApiResult.of(0, resultVO);
    }

    @Override
    public ApiResult<Void> enterMarketingNumber(SweepFloorMarketingNumberVO request) {

        ApiResult apiResult = checkActivityExist(request.getActivityId());
        if (!apiResult.isSuccess()) {
            return apiResult;
        }

        SweepFloorMarketingNumberQuery numberQuery = new SweepFloorMarketingNumberQuery();
        numberQuery.setActivityId(request.getActivityId());
        SweepFloorMarketingNumberDO marketingNumberDO = sweepFloorMarketingNumberMapper.get(numberQuery);
        if (marketingNumberDO == null) {
            SweepFloorMarketingNumberDO newMarketingNumberDO = new SweepFloorMarketingNumberDO();
            newMarketingNumberDO.setActivityId(request.getActivityId());
            if (!CollectionUtils.isEmpty(request.getBizDetails())) {
                newMarketingNumberDO.setCount(request.getBizDetails().stream().mapToInt(v -> v.getNum()).sum());
                newMarketingNumberDO.setDetail(GsonUtils.toJson(request.getBizDetails()));
            } else {
                newMarketingNumberDO.setCount(0);
                List<StallUpBizType> sweepFloorBizList = stallUpConfig.getConfig().getSweepFloorBizList();
                List<SweepFloorBizDetail> details = new ArrayList<>();
                for (StallUpBizType bizType : sweepFloorBizList) {
                    SweepFloorBizDetail bizDetail = new SweepFloorBizDetail();
                    bizDetail.setId(bizType.getId());
                    bizDetail.setName(bizType.getName());
                    bizDetail.setNum(0);
                    details.add(bizDetail);
                }
                newMarketingNumberDO.setDetail(GsonUtils.toJson(details));
            }
            newMarketingNumberDO.setRemark(request.getRemark());
            newMarketingNumberDO.setUserId(SmartGridContext.getUid());
            sweepFloorMarketingNumberMapper.insert(newMarketingNumberDO);
        } else {
            if (!CollectionUtils.isEmpty(request.getBizDetails())) {
                marketingNumberDO.setCount(request.getBizDetails().stream().mapToInt(v -> v.getNum()).sum());
                marketingNumberDO.setDetail(GsonUtils.toJson(request.getBizDetails()));
            } else {
                marketingNumberDO.setCount(0);
                List<StallUpBizType> sweepFloorBizList = stallUpConfig.getConfig().getSweepFloorBizList();
                List<SweepFloorBizDetail> details = new ArrayList<>();
                for (StallUpBizType bizType : sweepFloorBizList) {
                    SweepFloorBizDetail bizDetail = new SweepFloorBizDetail();
                    bizDetail.setId(bizType.getId());
                    bizDetail.setName(bizType.getName());
                    bizDetail.setNum(0);
                    details.add(bizDetail);
                }
                marketingNumberDO.setDetail(GsonUtils.toJson(details));
            }
            marketingNumberDO.setRemark(request.getRemark());
            sweepFloorMarketingNumberMapper.update(marketingNumberDO);
        }
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<SweepFloorMarketingNumberVO> getMarketingNumber(Long activityId) {
        SweepFloorActivityQuery sweepFloorActivityQuery = new SweepFloorActivityQuery();
        sweepFloorActivityQuery.setId(activityId);
        SweepFloorActivityDO activityDO = sweepFloorActivityMapper.get(sweepFloorActivityQuery);
        if (activityDO == null) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_ACTIVITY_NOT_EXIST);
        }

        SweepFloorMarketingNumberQuery numberQuery = new SweepFloorMarketingNumberQuery();
        numberQuery.setActivityId(activityId);
        SweepFloorMarketingNumberDO marketingNumberDO = sweepFloorMarketingNumberMapper.get(numberQuery);
        SweepFloorMarketingNumberVO numberVO = new SweepFloorMarketingNumberVO();
        if (marketingNumberDO == null) {
            List<StallUpBizType> sweepFloorBizList = stallUpConfig.getConfig().getSweepFloorBizList();
            List<SweepFloorBizDetail> details = new ArrayList<>();
            for (StallUpBizType bizType : sweepFloorBizList) {
                SweepFloorBizDetail bizDetail = new SweepFloorBizDetail();
                bizDetail.setId(bizType.getId());
                bizDetail.setName(bizType.getName());
                bizDetail.setNum(0);
                details.add(bizDetail);
            }
            numberVO.setBizDetails(details);
        } else {
            String detail = marketingNumberDO.getDetail();
            if (!StringUtils.isBlank(detail)) {
                List<SweepFloorBizDetail> details = GsonUtils.fromJsonToList(detail, SweepFloorBizDetail[].class);
                numberVO.setBizDetails(details);
            }
            numberVO.setRemark(marketingNumberDO.getRemark());
        }
        numberVO.setUserName(activityDO.getCreatorName());
        return ApiResult.of(0, numberVO);
    }

    @Override
    public ApiResult<Void> enterVisitRecording(SweepFloorVisitRecordingVO request) {
        ApiResult apiResult = checkActivityExist(request.getActivityId());
        if (!apiResult.isSuccess()) {
            return apiResult;
        }
        SweepFloorVisitRecordingDO visitRecordingDO = new SweepFloorVisitRecordingDO();
        BeanUtils.copyProperties(request, visitRecordingDO);
        visitRecordingDO.setMarketingUserId(SmartGridContext.getUid());
        visitRecordingDO.setMarketingUserName(HuaWeiUtil.getHuaweiUsername(SmartGridContext.getMobile()));
        visitRecordingDO.setGmtCreate(new Date());
        sweepFloorVisitRecordingMapper.insert(visitRecordingDO);
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<List<SweepFloorVisitRecordingVO>> getVisitRecording(String houseId, Long activityId) {
        SweepFloorVisitRecordingQuery query = new SweepFloorVisitRecordingQuery();
        query.setHouseId(houseId);
        List<SweepFloorVisitRecordingDO> doList = sweepFloorVisitRecordingMapper.find(query);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0, new ArrayList<>());
        }
//        List<Long> activityIds = doList.stream().map(SweepFloorVisitRecordingDO::getActivityId).collect(Collectors.toList());
//        SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
//        activityQuery.setIds(activityIds);
//        List<SweepFloorActivityDO> activityDOS = sweepFloorActivityMapper.find(activityQuery);
//        Map<Long, Integer> activityMap = activityDOS.stream().collect(Collectors.toMap(SweepFloorActivityDO::getId, SweepFloorActivityDO::getStatus));

        List<SweepFloorVisitRecordingVO> vos = new ArrayList<>(doList.size());
        for (SweepFloorVisitRecordingDO visitRecordingDO : doList) {
            SweepFloorVisitRecordingVO visitRecordingVO = new SweepFloorVisitRecordingVO();
            BeanUtils.copyProperties(visitRecordingDO, visitRecordingVO);
            visitRecordingVO.setVisitTime(visitRecordingDO.getGmtCreate());
            if (!StringUtils.isBlank(visitRecordingDO.getBusinessType())) {
                String businessType = visitRecordingDO.getBusinessType();
                boolean jsonList = GsonUtils.isJsonList(businessType, StallUpBizType[].class);
                if (jsonList) {
                    List<StallUpBizType> bizTypeList = GsonUtils.fromJsonToList(businessType, StallUpBizType[].class);
                    if (!CollectionUtils.isEmpty(bizTypeList)) {
                        List<String> names = bizTypeList.stream().map(StallUpBizType::getName).collect(Collectors.toList());
                        String name = names.stream().collect(Collectors.joining(","));
                        visitRecordingVO.setBusinessType(name);
                    }else {
                        visitRecordingVO.setBusinessType("");
                    }
                }else {
                    visitRecordingVO.setBusinessType(businessType);
                }
            }
            //Integer status = activityMap.get(visitRecordingDO.getActivityId());
            if (activityId.equals(visitRecordingDO.getActivityId())) {
                visitRecordingVO.setType(CommonFlagEnum.ONE_FLAG.getId());
            } else {
                visitRecordingVO.setType(CommonFlagEnum.ZERO_FLAG.getId());
            }
            vos.add(visitRecordingVO);
        }
        return ApiResult.of(0, vos);
    }

    @Override
    public ApiResult<List<SweepFloorVisitRecordingVO>> getVisitRecordingByActivityId(Long activityId) {
        ApiResult apiResult = checkActivityExist(activityId);
        if (!apiResult.isSuccess()) {
            return apiResult;
        }
        SweepFloorVisitRecordingQuery query = new SweepFloorVisitRecordingQuery();
        List<Long> activityIds = new ArrayList<>();
        activityIds.add(activityId);
        query.setActivityIds(activityIds);
        List<SweepFloorVisitRecordingDO> doList = sweepFloorVisitRecordingMapper.find(query);
        if (CollectionUtils.isEmpty(doList)) {
            return ApiResult.of(0, new ArrayList<>());
        }
        List<SweepFloorVisitRecordingVO> vos = new ArrayList<>(doList.size());
        for (SweepFloorVisitRecordingDO visitRecordingDO : doList) {
            SweepFloorVisitRecordingVO visitRecordingVO = new SweepFloorVisitRecordingVO();
            BeanUtils.copyProperties(visitRecordingDO, visitRecordingVO);
            visitRecordingVO.setVisitTime(visitRecordingDO.getGmtCreate());
            if (!StringUtils.isBlank(visitRecordingDO.getBusinessType())) {
                String businessType = visitRecordingDO.getBusinessType();
                //visitRecordingVO.setBusinessType(GsonUtils.fromJsonToList(businessType, StallUpBizType[].class));
                boolean jsonList = GsonUtils.isJsonList(businessType, StallUpBizType[].class);
                if (jsonList) {
                    List<StallUpBizType> bizTypeList = GsonUtils.fromJsonToList(businessType, StallUpBizType[].class);
                    if (!CollectionUtils.isEmpty(bizTypeList)) {
                        List<String> names = bizTypeList.stream().map(StallUpBizType::getName).collect(Collectors.toList());
                        String name = names.stream().collect(Collectors.joining(","));
                        visitRecordingVO.setBusinessType(name);
                    }else {
                        visitRecordingVO.setBusinessType("");
                    }
                }else {
                    visitRecordingVO.setBusinessType(businessType);
                }
            }

            vos.add(visitRecordingVO);
        }
        return ApiResult.of(0, vos);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> satrtSign(SignRecordQuery request) {

        ApiResult apiResult = checkActivityExist(request.getActivityId());
        if (!apiResult.isSuccess()) {
            return apiResult;
        }

        ApiResult checkStatusResult = checkActivityStatusWhenStartSign(request.getActivityId());
        if (checkStatusResult != null) {
            return checkStatusResult;
        }

        //更新扫楼活动状态
        updateActivityStatus(request.getActivityId(), SweepFloorStatusEnum.PROCESSING.getId(),new Date(),null,true);
        //记录打卡
        SignRecordDO signRecordDO = getSignRecord(request.getActivityId());

        if (signRecordDO == null) {
            SignRecordDO insertDO = new SignRecordDO();
            insertDO.setActivityId(request.getActivityId());
            insertDO.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
            insertDO.setStartTime(new Date());
            insertDO.setStartLocation(GsonUtils.toJson(request.getLocationDetailVO()));
            insertDO.setUserId(SmartGridContext.getUid());
            insertDO.setMobile(SmartGridContext.getMobile());
            insertDO.setUserName(SmartGridContext.getUserName());
            signRecordMapper.insert(insertDO);
        } else {
            SignRecordDO updateDO = new SignRecordDO();
            updateDO.setId(signRecordDO.getId());
            updateDO.setStartTime(new Date());
            updateDO.setStartLocation(GsonUtils.toJson(request.getLocationDetailVO()));
            signRecordMapper.update(updateDO);
        }
        return ApiResult.of(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult<Void> endSign(SignRecordQuery request) {
        ApiResult apiResult = checkActivityExist(request.getActivityId());
        if (!apiResult.isSuccess()) {
            return apiResult;
        }
        SweepFloorActivityDO activityDO = (SweepFloorActivityDO) apiResult.getData();
        String reqLocation = request.getLocationDetailVO().getLocation();
        //todo
        boolean flag = true;
        ApiResult distanceWhenSign = checkDistanceWhenSign(activityDO.getLocation(), reqLocation);
        if (!distanceWhenSign.isSuccess()) {
            flag = false;
        }
        //更新活动状态
        if (activityDO.getStatus() != SweepFloorStatusEnum.PROCESSING.getId()) {
            log.error("endSign error status invalid,id = {},status = {}", activityDO.getId(), activityDO.getStatus());
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_STATUS_ERROR_END_SIGN);
        }
        updateActivityStatus(request.getActivityId(), SweepFloorStatusEnum.END.getId(),null,new Date(),flag);
        //更新打卡记录
        SignRecordDO signRecordDO = getSignRecord(request.getActivityId());

        SignRecordDO updateDO = new SignRecordDO();
        updateDO.setId(signRecordDO.getId());
        updateDO.setEndTime(new Date());
        updateDO.setEndLocation(GsonUtils.toJson(request.getLocationDetailVO()));
        updateDO.setImgUrl(GsonUtils.toJson(request.getImgUrl()));
        updateDO.setRemark(request.getRemark());
        signRecordMapper.update(updateDO);

        return ApiResult.of(0);
    }

    @Override
    public ApiResult<ListVO<SweepFloorActivityVO>> getSweepFloorActivity(Integer status, Integer pageSize,
                                                                         Integer currentPage, Long satrtTime, Long endTime) {
        //查询活动信息
        SweepFloorActivityQuery request = new SweepFloorActivityQuery();
        request.setCreator(SmartGridContext.getUid());
        request.setStatus(status);
        if (status.equals(SweepFloorStatusEnum.END.getId())) {
            request.setPageSize(pageSize);
            request.setCurrentPage(currentPage);
            request.setQueryTotal(true);
            request.setStatus(null);
            List<Integer> statusList = new ArrayList<>();
            statusList.add(SweepFloorStatusEnum.END.getId());
            statusList.add(SweepFloorStatusEnum.ABNORMAL_END.getId());
            statusList.add(SweepFloorStatusEnum.CANCEL.getId());
            request.setStatusList(statusList);
        }
        long count = 0L;
        List<SweepFloorActivityDO> activityDOS = null;
        if (satrtTime == null && endTime == null) {
            activityDOS = sweepFloorActivityMapper.find(request);
        } else {
            SignRecordQuery signRecordQuery = new SignRecordQuery();
            signRecordQuery.setPageSize(pageSize);
            signRecordQuery.setCurrentPage(currentPage);
            signRecordQuery.setQueryTotal(true);
            signRecordQuery.setStartTime(new Date(satrtTime));
            signRecordQuery.setEndTime(new Date(endTime));
            signRecordQuery.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
            signRecordQuery.setUserId(SmartGridContext.getUid());
            List<SignRecordDO> signRecordDOS = signRecordMapper.find(signRecordQuery);
            if (CollectionUtils.isEmpty(signRecordDOS)) {
                return ApiResult.of(0, ListVO.list(new ArrayList<>(), 0));
            }
            List<Long> idList = signRecordDOS.stream().map(SignRecordDO::getActivityId).collect(Collectors.toList());
            SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
            activityQuery.setIds(idList);
            activityDOS = sweepFloorActivityMapper.find(activityQuery);

            SignRecordQuery countQuery = new SignRecordQuery();
            countQuery.setStartTime(new Date(satrtTime));
            countQuery.setEndTime(new Date(endTime));
            countQuery.setUserId(SmartGridContext.getUid());
            countQuery.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
            count = signRecordMapper.count(countQuery);

        }
        if (CollectionUtils.isEmpty(activityDOS)) {
            return ApiResult.of(0, ListVO.list(new ArrayList<>(), 0));
        }
        List<SweepFloorActivityVO> vos = new ArrayList<>(activityDOS.size());
        for (SweepFloorActivityDO activityDO : activityDOS) {
            SweepFloorActivityVO activityVO = new SweepFloorActivityVO();
            BeanUtils.copyProperties(activityDO, activityVO);
            vos.add(activityVO);
        }
        List<String> communityIds = activityDOS.stream().map(SweepFloorActivityDO::getCommunityId).collect(Collectors.toList());
        //已结束的活动不需要查询华为接口
        if (status != null && status.equals(SweepFloorStatusEnum.END.getId())) {
            //查询实际打卡时间
            List<Long> longList = activityDOS.stream().map(SweepFloorActivityDO::getId).collect(Collectors.toList());
            SignRecordQuery signRecordQuery = new SignRecordQuery();
            signRecordQuery.setActivityIds(longList);
            signRecordQuery.setBizType(1);
            List<SignRecordDO> signRecordDOS = signRecordMapper.find(signRecordQuery);
            if (!CollectionUtils.isEmpty(signRecordDOS)) {
                Map<Long, SignRecordDO> recordDOMap = signRecordDOS.stream().collect(Collectors.toMap(SignRecordDO::getActivityId, account -> account));
                for (SweepFloorActivityVO activityVO : vos) {
                    SignRecordDO signRecordDO = recordDOMap.get(activityVO.getId());
                    if (signRecordDO != null) {
                        activityVO.setStartTime(signRecordDO.getStartTime());
                        activityVO.setEndTime(signRecordDO.getEndTime());
                    }
                }
            }
            if (count == 0L) {
                SweepFloorActivityQuery countQuery = new SweepFloorActivityQuery();
                List<Integer> statusList = new ArrayList<>();
                statusList.add(status);
                statusList.add(SweepFloorStatusEnum.ABNORMAL_END.getId());
                statusList.add(SweepFloorStatusEnum.CANCEL.getId());
                countQuery.setStatusList(statusList);
                countQuery.setCreator(SmartGridContext.getUid());
                count = sweepFloorActivityMapper.count(countQuery);
            }
            //查询办理量
            SweepFloorVisitRecordingQuery visitRecordingQuery = new SweepFloorVisitRecordingQuery();
            visitRecordingQuery.setActivityIds(longList);
            visitRecordingQuery.setSuccessFlag(CommonFlagEnum.ONE_FLAG.getId());
            List<SweepFloorVisitRecordingDO> recordingDOS = sweepFloorVisitRecordingMapper.find(visitRecordingQuery);
            if (!CollectionUtils.isEmpty(recordingDOS)) {
                Map<Long, List<SweepFloorVisitRecordingDO>> listMap = recordingDOS.stream().collect(Collectors.groupingBy(SweepFloorVisitRecordingDO::getActivityId));
                for (SweepFloorActivityVO activityVO : vos) {
                    List<SweepFloorVisitRecordingDO> visitRecordingDOS = listMap.get(activityVO.getId());
                    if (!CollectionUtils.isEmpty(visitRecordingDOS)) {
                        activityVO.setHouseCount(visitRecordingDOS.size());
                    } else {
                        activityVO.setHouseCount(0);
                    }
                }
            } else {
                for (SweepFloorActivityVO activityVO : vos) {
                    activityVO.setHouseCount(0);
                }
            }
            SweepFloorMarketingNumberQuery numberQuery = new SweepFloorMarketingNumberQuery();
            numberQuery.setActivityIds(longList);
            numberQuery.setUserId(SmartGridContext.getUid());
            List<SweepFloorMarketingNumberDO> numberDOS = sweepFloorMarketingNumberMapper.find(numberQuery);
            if (!CollectionUtils.isEmpty(numberDOS)) {
                for (SweepFloorActivityVO activityVO : vos) {
                    for (SweepFloorMarketingNumberDO numberDO : numberDOS) {
                        if (activityVO.getId().equals(numberDO.getActivityId())) {
                            activityVO.setBusinessCount(numberDO.getCount());
                        }
                    }
                    if (activityVO.getBusinessCount() == null) {
                        activityVO.setBusinessCount(0);
                    }
                }
            }

            return ApiResult.of(0, ListVO.list(vos, count));
        }
        HuaweiCellAndBuildingsRequest huaweiRequest = new HuaweiCellAndBuildingsRequest();
        huaweiRequest.setCellIds(communityIds);
        ApiResult<List<HuaweiCellsAndBuildingsResponse>> apiResult = huaWeiService.getCellsAndBuildings(huaweiRequest);
        if (apiResult == null || !apiResult.isSuccess()) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
        }
        List<HuaweiCellsAndBuildingsResponse> apiResultData = apiResult.getData();

        if (!CollectionUtils.isEmpty(apiResultData)) {
            for (SweepFloorActivityVO activityVO : vos) {
                for (HuaweiCellsAndBuildingsResponse buildingsResponse : apiResultData) {
                    if (activityVO.getCommunityId().equals(buildingsResponse.getCellId())) {
                        List<HuaweiBuildingResponse> buildingList = buildingsResponse.getBuildingList();
                        if (CollectionUtils.isEmpty(buildingList)) {
                            activityVO.setBuildingCount(0);
                            activityVO.setHouseholderCount(0);
                            activityVO.setRemainingPortCount(0);
                        } else {
                            int householderCount = 0;
                            int remainingPortCount = 0;
                            for (HuaweiBuildingResponse buildingResponse:buildingList) {
                                if (buildingResponse.getLiveCount() != null) {
                                    householderCount += buildingResponse.getLiveCount();
                                }
                                if (buildingResponse.getRemainingPortCount() != null) {
                                    remainingPortCount += buildingResponse.getRemainingPortCount();
                                }
                            }
                            activityVO.setBuildingCount(buildingList.size());
                            activityVO.setHouseholderCount(householderCount);
                            activityVO.setRemainingPortCount(remainingPortCount);
                        }
                    }
                }
            }
        }
        return ApiResult.of(0, ListVO.list(vos, vos.size()));
    }

    @Override
    public ApiResult<List<BuildingVO>> getBuildings(SweepFloorBuildingQuery request) {
        List<String> cellIds = new ArrayList<>();
        cellIds.add(request.getCommunityId());
        HuaweiCellAndBuildingsRequest huaweiRequest = new HuaweiCellAndBuildingsRequest();
        huaweiRequest.setCellIds(cellIds);
        if (!StringUtils.isBlank(request.getBuildingId())) {
            huaweiRequest.setBuildingId(request.getBuildingId());
        }
        ApiResult<List<HuaweiCellsAndBuildingsResponse>> apiResult = huaWeiService.getCellsAndBuildings(huaweiRequest);
        if (apiResult == null || !apiResult.isSuccess()) {
            log.error("[getBuildings] huaWeiService.getCellsAndBuildings error,apiResult = {}", apiResult);
            return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
        }
        List<BuildingVO> vos = new ArrayList<>();
        List<HuaweiCellsAndBuildingsResponse> buildingsData = apiResult.getData();
        if (CollectionUtils.isEmpty(buildingsData)) {
            return ApiResult.of(0, new ArrayList<>());
        }
        List<BuildingVO> numData = new ArrayList<>();
        List<BuildingVO> stringData = new ArrayList<>();
        for (HuaweiCellsAndBuildingsResponse response : buildingsData) {
            List<HuaweiBuildingResponse> buildingList = response.getBuildingList();
            for (HuaweiBuildingResponse buildingResponse : buildingList) {
                BuildingVO buildingVO = new BuildingVO();
                buildingVO.setCommunityId(response.getCellId());
                buildingVO.setCommunityName(response.getCellName());
                buildingVO.setBuildingId(buildingResponse.getBuildingId());
                buildingVO.setBuildingName(buildingResponse.getBuildingName());
                int convert2Int = -1;
                if (!StringUtils.isBlank(buildingResponse.getBuildingName())) {
                    String buildingName = buildingResponse.getBuildingName();
                    convert2Int = checkCanConvert2Int(buildingName);

                }
                if (!StringUtils.isBlank(buildingResponse.getBroadbandType())) {
                    String broadbandType = buildingResponse.getBroadbandType();
                    String[] split = broadbandType.split(",");
                    buildingVO.setBroadbandType(Arrays.asList(split));
                }else {
                    buildingVO.setBroadbandType(new ArrayList<>());
                }
                buildingVO.setBroadbandUserCount(buildingResponse.getBroadbandUserCount());
                buildingVO.setFloorNumber(buildingResponse.getFloorNumber());
                buildingVO.setHouseholderCount(buildingResponse.getLiveCount());
                buildingVO.setHouseholderCountEveryFloor(buildingResponse.getHouseholderCount());
                buildingVO.setRemainingPortCount(buildingResponse.getRemainingPortCount());
                buildingVO.setUnitCount(buildingResponse.getUnitCount());
                buildingVO.setPenetrationRate(buildingResponse.getPenetrationRate());
                buildingVO.setLabels(buildingResponse.getPenetrationLabel());
                if (convert2Int != -1) {
                    if (convert2Int != 0) {
                        String buildingName = buildingResponse.getBuildingName();
                        buildingVO.setStringSort(buildingName.substring(0, convert2Int));
                        numData.add(buildingVO);
                    } else {
                        stringData.add(buildingVO);
                    }
                } else {
                    buildingVO.setStringSort(buildingResponse.getBuildingName());
                    numData.add(buildingVO);
                }
                vos.add(buildingVO);
            }
        }
        Collections.sort(numData);
        numData.addAll(stringData);
        return ApiResult.of(0, numData);
    }

    private int checkCanConvert2Int(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!numList.contains(str.charAt(i)) && !stringList.contains(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ApiResult<BuildingVO> addBuilding(BuildingVO request) {
        HuaweiBuildingRequest huaweiRequest = new HuaweiBuildingRequest();
        buildingVoToHuawei(huaweiRequest, request);
        ApiResult<HuaWeiAddBuildingResponse> apiResult = huaWeiService.addBuilding(huaweiRequest);
        if (!apiResult.isSuccess() && apiResult.getCode() != 301) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
        }else if (apiResult.getCode() == 301) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_BUILDING_NAME_DUPLICATE);
        }
        BuildingVO buildingVO = new BuildingVO();
        buildingVO.setBuildingId(apiResult.getData().getData().getBuildingId());
        buildingVO.setBuildingName(request.getBuildingName());
        buildingVO.setCommunityId(request.getCommunityId());
        return ApiResult.of(0,buildingVO);
    }

    @Override
    public ApiResult<Void> updateBuilding(BuildingVO request) {
        HuaweiBuildingRequest huaweiRequest = new HuaweiBuildingRequest();
        buildingVoToHuawei(huaweiRequest, request);
        return huaWeiService.updateBuilding(huaweiRequest);
    }

    @Override
    public ApiResult<HouseholdVO> addHousehold(HouseholdVO request) {
        HuaweiTenantRequest huaweiRequest = new HuaweiTenantRequest();
        householdVOToHuawei(huaweiRequest, request);
        ApiResult<HuaWeiAddTenantsResponse> apiResult = huaWeiService.addBuildingTenants(huaweiRequest);

        if (!apiResult.isSuccess() && apiResult.getCode() != 302) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
        }else if (apiResult.getCode() == 302) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_HOUSE_NUMBER_DUPLICATE);
        }
        HuaWeiAddTenantsResponse apiResultData = apiResult.getData();
        HouseholdVO householdVO = new HouseholdVO();
        householdVO.setHouseId(apiResultData.getData().getHouseId());
        householdVO.setBuildingName(request.getBuildingName());
        householdVO.setBuildingId(request.getBuildingId());
        householdVO.setHouseNumber(request.getHouseNumber());
        householdVO.setUnitId(request.getUnitId());
        householdVO.setUnitName(request.getUnitName());
        return ApiResult.of(0,householdVO,apiResult.getMsg());
    }

    @Override
    public ApiResult<Void> updateHousehold(HouseholdVO request) {
        HuaweiTenantRequest huaweiRequest = new HuaweiTenantRequest();
        householdVOToHuawei(huaweiRequest, request);
        huaweiRequest.setHouseId(request.getHouseId());
        return huaWeiService.updateBuildingTenants(huaweiRequest);
    }

    @Override
    public ApiResult<List<BuildingDetailsVO>> getBuildingPlan(SweepFloorBuildingQuery request) {
        String buildingId = request.getBuildingId();
        String communityId = request.getCommunityId();
        HuaweiBuildingDetailListRequest huaweiRequest = new HuaweiBuildingDetailListRequest();
        if (request.getType() == 1) {
            huaweiRequest.setBuildingId(buildingId);
            huaweiRequest.setTreeFlag(true);
            huaweiRequest.setTreeLevel(4);
        } else if (request.getType() == 2) {
            //查询树形地址
            huaweiRequest.setCellId(communityId);
            huaweiRequest.setTreeFlag(true);
            huaweiRequest.setTreeLevel(3);
        }
        ApiResult<List<HuaweiBuildingDetailsListResponse>> listApiResult = huaWeiService.queryBuildingDetailList(huaweiRequest);

        if (listApiResult == null || !listApiResult.isSuccess()) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
        }
        List<HuaweiBuildingDetailsListResponse> apiResultData = listApiResult.getData();
        if (CollectionUtils.isEmpty(apiResultData)) {
            return ApiResult.of(0, new ArrayList<>());
        }

        List<BuildingDetailsVO> detailsVOS = new ArrayList<>();
        for (HuaweiBuildingDetailsListResponse huaweiResponse : apiResultData) {
            BuildingDetailsVO buildingDetailsVO = new BuildingDetailsVO();
            buildingDetailsVO.setBuildingId(huaweiResponse.getBuildingId());
            buildingDetailsVO.setBuildingName(huaweiResponse.getBuildingName());
            List<HuaweiUnitResponse> unitList = huaweiResponse.getUnitList();
            if (!CollectionUtils.isEmpty(unitList)) {
                List<UnitVO> unitVOS = new ArrayList<>();
                for (HuaweiUnitResponse huaweiUnitResponse : unitList) {
                    UnitVO unitVO = new UnitVO();
                    unitVO.setUnitId(huaweiUnitResponse.getUnitId());
                    unitVO.setUnitName(huaweiUnitResponse.getUnitName());
                    List<HuaweiHouseResponse> huaweiHouseList = huaweiUnitResponse.getHouseList();
                    if (!CollectionUtils.isEmpty(huaweiHouseList)) {
                        List<HouseholdVO> householdVOS = new ArrayList<>();
                        for (HuaweiHouseResponse huaweiHouseResponse : huaweiHouseList) {
                            HouseholdVO householdVO = new HouseholdVO();
                            householdVO.setBroadbandExpireTime(DateUtils.stringtoDate(huaweiHouseResponse.getBroadbandExpireTime(), "yyyy-MM-dd"));
                            householdVO.setBroadbandFlag(huaweiHouseResponse.getBroadbandFlag());
                            householdVO.setHouseId(huaweiHouseResponse.getHouseId());
                            householdVO.setHouseNumber(huaweiHouseResponse.getHouseNumber());
                            if (!StringUtils.isBlank(huaweiHouseResponse.getServiceProvider())) {
                                String serviceProvider = huaweiHouseResponse.getServiceProvider();
                                String[] split = serviceProvider.split(",");
                                householdVO.setServiceProvider(Arrays.asList(split));
                            }else {
                                householdVO.setServiceProvider(new ArrayList<>());
                            }
                            List<HuaweiContactResponse> concactList = huaweiHouseResponse.getConcactList();
                            if (!CollectionUtils.isEmpty(concactList)) {
                                List<FamilyMember> members = new ArrayList<>();
                                for (HuaweiContactResponse huaweiContactResponse : concactList) {
                                    FamilyMember member = new FamilyMember();
                                    if (!StringUtils.isBlank(huaweiContactResponse.getContactMobile())) {
                                        String mobile = AESUtil.decrypt(huaweiContactResponse.getContactMobile(), aeskey);
                                        member.setContactMobile(mobile);
                                        member.setEncryContactMobile(huaweiContactResponse.getContactMobile());
                                    }
                                    member.setContactName(huaweiContactResponse.getContactName());
                                    member.setType(Integer.valueOf(huaweiContactResponse.getType()));
                                    members.add(member);
                                }
                                //设置缓存失效时间24小时
                                redisService.set(huaweiResponse.getBuildingId() + huaweiHouseResponse.getHouseNumber(), GsonUtils.toJson(members), 24*60*60);
                                householdVO.setFamilyMembers(members);
                            }
                            householdVOS.add(householdVO);
                        }
                        buildHouseLabels(request.getCommunityId(), householdVOS);
                        unitVO.setHouseholdVOS(householdVOS);
                    }
                    unitVOS.add(unitVO);
                }
                buildingDetailsVO.setUnitVOS(unitVOS);
            }
            detailsVOS.add(buildingDetailsVO);
        }
        return ApiResult.of(0, detailsVOS);
    }

    @Override
    public ApiResult<List<HouseholdVO>> getBuildingHouseholds(SweepFloorBuildingQuery request) {
        String buildingId = request.getBuildingId();
        HuaweiBuildingDetailListRequest huaweiRequest = new HuaweiBuildingDetailListRequest();
        huaweiRequest.setBuildingId(buildingId);
        huaweiRequest.setTreeFlag(true);
        huaweiRequest.setTreeLevel(4);
        ApiResult<List<HuaweiBuildingDetailsListResponse>> listApiResult = huaWeiService.queryBuildingDetailList(huaweiRequest);
        if (listApiResult == null || !listApiResult.isSuccess()) {
            log.error("[getBuildingHouseholds] huaWeiService error apiResult = {}", listApiResult);
            return ApiResultWrapper.fail(SweepFloorErrorCodes.BASE_ERROR);
        }
        List<HuaweiBuildingDetailsListResponse> apiResultData = listApiResult.getData();
        if (CollectionUtils.isEmpty(apiResultData)) {
            return ApiResult.of(0, new ArrayList<>());
        }
        List<HouseholdVO> householdVOS = new ArrayList<>();
        for (HuaweiBuildingDetailsListResponse huaweiBuildingResponse : apiResultData) {
            List<HuaweiUnitResponse> unitList = huaweiBuildingResponse.getUnitList();
            if (!CollectionUtils.isEmpty(unitList)) {
                for (HuaweiUnitResponse huaweiUnitResponse : unitList) {
                    List<HuaweiHouseResponse> houseList = huaweiUnitResponse.getHouseList();
                    if (!CollectionUtils.isEmpty(houseList)) {
                        for (HuaweiHouseResponse huaweiHouseResponse : houseList) {
                            HouseholdVO householdVO = new HouseholdVO();
                            householdVO.setHouseId(huaweiHouseResponse.getHouseId());
                            householdVO.setHouseNumber(huaweiHouseResponse.getHouseNumber());
                            householdVO.setBroadbandFlag(huaweiHouseResponse.getBroadbandFlag());
                            householdVO.setBroadbandExpireTime(DateUtils.stringtoDate(huaweiHouseResponse.getBroadbandExpireTime(),"yyyy-MM-dd"));
                            if (!StringUtils.isBlank(huaweiHouseResponse.getServiceProvider())) {
                                String serviceProvider = huaweiHouseResponse.getServiceProvider();
                                String[] split = serviceProvider.split(",");
                                householdVO.setServiceProvider(Arrays.asList(split));
                            }else {
                                householdVO.setServiceProvider(new ArrayList<>());
                            }
                            householdVO.setBuildingId(huaweiBuildingResponse.getBuildingId());
                            householdVO.setBuildingName(huaweiBuildingResponse.getBuildingName());
                            householdVO.setUnitId(huaweiUnitResponse.getUnitId());
                            householdVO.setUnitName(huaweiUnitResponse.getUnitName());
                            List<HuaweiContactResponse> concactList = huaweiHouseResponse.getConcactList();
                            if (!CollectionUtils.isEmpty(concactList)) {
                                List<FamilyMember> members = new ArrayList<>(concactList.size());
                                for (HuaweiContactResponse contactResponse : concactList) {
                                    FamilyMember member = new FamilyMember();
                                    if (!StringUtils.isBlank(contactResponse.getContactMobile())) {
                                        String mobile = AESUtil.decrypt(contactResponse.getContactMobile(), aeskey);
                                        member.setContactMobile(mobile);
                                        member.setEncryContactMobile(contactResponse.getContactMobile());
                                    }
                                    member.setType(Integer.valueOf(contactResponse.getType()));
                                    member.setContactName(contactResponse.getContactName());
                                    members.add(member);
                                }
                                householdVO.setFamilyMembers(members);
                            }
                            householdVOS.add(householdVO);
                        }
                    }
                }
            }
        }
        buildHouseLabels(request.getCommunityId(), householdVOS);
        return ApiResult.of(0, householdVOS);
    }

    private void buildHouseLabels(String communityId, List<HouseholdVO> householdVOS) {
        //拼装最新走访信息
        SweepFloorVisitRecordingQuery query = new SweepFloorVisitRecordingQuery();
        List<String> houseIds = householdVOS.stream().map(HouseholdVO::getHouseId).collect(Collectors.toList());
        query.setHouseIds(houseIds);
        query.setCommunityId(communityId);
        List<SweepFloorVisitRecordingDO> recordingDOS = sweepFloorVisitRecordingMapper.findNewLatest(query);
        if (!CollectionUtils.isEmpty(recordingDOS)) {
            for (SweepFloorVisitRecordingDO recordingDO : recordingDOS) {
                for (HouseholdVO householdVO : householdVOS) {
                    if (recordingDO.getHouseId().equals(householdVO.getHouseId())) {
                        List<String> labels = new ArrayList<>();
                        householdVO.setVisitTime(recordingDO.getGmtCreate());
                        if (recordingDO.getSuccessFlag() == 1) {
                            //todo
                            labels.add("market_success");
                        } else {
                            labels.add("market_fail");
                        }
                        if (recordingDO.getComplaintSensitiveCustomersFlag() == 1) {
                            //todo
                            labels.add("complaint_sensitive_customers");
                        }
                        householdVO.setLabels(labels);
                    }
                }
            }
        }
    }

    @Override
    public ApiResult<IndexInfoResponse> getIndexInfo(long uid) {
        IndexInfoResponse indexInfoResponse = new IndexInfoResponse();
        SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
        activityQuery.setCreator(String.valueOf(uid));
        List<Integer> statusList = new ArrayList<>();
        statusList.add(SweepFloorStatusEnum.PROCESSING.getId());
        statusList.add(SweepFloorStatusEnum.NOT_START.getId());
        activityQuery.setStatusList(statusList);
        List<SweepFloorActivityDO> sweepFloorActivityDOS = sweepFloorActivityMapper.find(activityQuery);

        //待办事项
        if (!CollectionUtils.isEmpty(sweepFloorActivityDOS)) {
            indexInfoResponse.setTodayToDo(Long.valueOf(sweepFloorActivityDOS.size()));
            indexInfoResponse.setWeekToDo(Long.valueOf(sweepFloorActivityDOS.size()));
            for (SweepFloorActivityDO activityDO : sweepFloorActivityDOS) {
                if (activityDO.getStatus().equals(SweepFloorStatusEnum.PROCESSING.getId())) {
                    SweepFloorActivityVO sweepFloorActivityVO = new SweepFloorActivityVO();
                    BeanUtils.copyProperties(activityDO, sweepFloorActivityVO);
                    indexInfoResponse.setSweepFloorActivityVO(sweepFloorActivityVO);
                }
            }
        } else {
            indexInfoResponse.setTodayToDo(0L);
            indexInfoResponse.setWeekToDo(0L);
        }
        //查询本月已办
        SweepFloorActivityQuery endActivityQuery = new SweepFloorActivityQuery();
//        endActivityQuery.setStatus(SweepFloorStatusEnum.END.getId());
        List<Integer> endStatusList = new ArrayList<>();
        endStatusList.add(SweepFloorStatusEnum.END.getId());
        endStatusList.add(SweepFloorStatusEnum.ABNORMAL_END.getId());
        endActivityQuery.setStatusList(endStatusList);
        endActivityQuery.setCreator(String.valueOf(uid));
        List<SweepFloorActivityDO> activityDOS = sweepFloorActivityMapper.find(endActivityQuery);
        if (!CollectionUtils.isEmpty(activityDOS)) {
            Date thisMonthFirstDay = DateUtils.getThisMonthFirstDay();
            Long count = 0L;
            for (SweepFloorActivityDO floorActivityDO : activityDOS) {
                if (floorActivityDO.getGmtModified().after(thisMonthFirstDay) &&
                        (floorActivityDO.getStatus().equals(SweepFloorStatusEnum.END.getId())
                                || floorActivityDO.getStatus().equals(SweepFloorStatusEnum.ABNORMAL_END.getId()))) {
                    count += 1L;
                }
            }
            indexInfoResponse.setMonthDone(count);
        } else {
            indexInfoResponse.setMonthDone(0L);
        }
        return ApiResult.success(indexInfoResponse);
    }

    @Override
    public ApiResult<List<String>> queryBizType() {
        BusinessConfigQuery query = new BusinessConfigQuery();
        query.setId(2L);
        BusinessConfigDO configDO = businessConfigMapper.get(query);
        if (configDO == null || StringUtils.isBlank(configDO.getContent())) {
            return ApiResult.of(0, new ArrayList<>());
        }
        String content = configDO.getContent();
        List<String> strings = GsonUtils.fromJsonToList(content, String[].class);
        return ApiResult.of(0, strings);
    }

    @Override
    public ApiResult<List<FamilyMember>> getFamilyMembers(String buildingId, String unitId, String houseNumber) {
        String redisValue = redisService.get(buildingId + houseNumber);
        if (!StringUtils.isBlank(redisValue)) {
            List<FamilyMember> list = GsonUtils.fromJsonToList(redisValue, FamilyMember[].class);
            return ApiResult.of(0, list);
        }
        SweepFloorBuildingQuery request = new SweepFloorBuildingQuery();
        request.setType(1);
        request.setBuildingId(buildingId);
        ApiResult<List<BuildingDetailsVO>> buildingPlan = getBuildingPlan(request);
        String redisValue02 = redisService.get(buildingId + houseNumber);
        if (!StringUtils.isBlank(redisValue02)) {
            List<FamilyMember> list = GsonUtils.fromJsonToList(redisValue02, FamilyMember[].class);
            return ApiResult.of(0, list);
        } else {
            return ApiResult.of(0, new ArrayList<>());
        }
    }

    @Override
    public void refreshSmartGridActivity(String mobile) {
        log.info("[refreshSmartGridActivity] 开始刷t_smart_grid_activity表数据，mobile:{}", mobile);

        SweepFloorActivityQuery sweepFloorActivityQuery = new SweepFloorActivityQuery();
        sweepFloorActivityQuery.setMobile(mobile);
        List<SweepFloorActivityDO> sweepFloorActivityDOList = sweepFloorActivityMapper.find(sweepFloorActivityQuery);
        if (CollectionUtils.isEmpty(sweepFloorActivityDOList)) {
            log.info("[refreshSmartGridActivity] 活动不存在，mobile:{}", mobile);
            return;
        }

        //根据手机号查询网格id
        HuaWeiRequest huaWeiRequest = HuaWeiRequest.builder().mobile(mobile).build();
        ApiResult<List<GridUserRoleDetail>> gridUserInfoResult = null;
        try {
            gridUserInfoResult = huaWeiService.getGridUserInfo(huaWeiRequest);
        }catch (ApiException e) {
            log.info("[refreshSmartGridActivity] huaWeiService.getGridUserInfo ApiException ，mobile:{}", mobile);
            refreshSmartGridIdWhenNotGridUser(mobile,sweepFloorActivityDOList);
            return;
        }

        if (gridUserInfoResult == null || !gridUserInfoResult.isSuccess()) {
            log.info("[refreshSmartGridActivity] huaWeiService.getGridUserInfo error ，mobile:{}", mobile);
            return;
        }

        if (CollectionUtils.isEmpty(gridUserInfoResult.getData())) {
            log.info("[refreshSmartGridActivity] huaWeiService.getGridUserInfo data is null ，mobile:{}", mobile);
            refreshSmartGridIdWhenNotGridUser(mobile,sweepFloorActivityDOList);
            return;
        }

//        for (GridUserRoleDetail gridUserRoleDetail : gridUserInfoResult.getData()) {
//            List<String> gridIds = new ArrayList<>();
//            for (SweepFloorActivityDO sweepFloorActivityDO : sweepFloorActivityDOList) {
//                Long activityId = sweepFloorActivityDO.getId();
//                String gridId = gridUserRoleDetail.getId();
//                gridIds.add(gridId);
//                //判断记录是否存在
//                SmartGridActivityQuery smartGridActivityQuery = new SmartGridActivityQuery();
//                smartGridActivityQuery.setActivityId(activityId);
//                smartGridActivityQuery.setGridId(gridId);
//                SmartGridActivityDO old = smartGridActivityMapper.get(smartGridActivityQuery);
//                if (old != null) {
//                    log.info("[refreshSmartGridActivity] 数据已存在，mobile:{},activityId:{},gridId:{}", mobile, activityId, gridId);
//                    continue;
//                }
//
//                SmartGridActivityDO smartGridActivityDO = new SmartGridActivityDO();
//                smartGridActivityDO.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
//                smartGridActivityDO.setActivityId(activityId);
//                smartGridActivityDO.setGridId(gridId);
//                smartGridActivityDO.setGridName(gridUserRoleDetail.getName());
//                smartGridActivityMapper.insert(smartGridActivityDO);
//                log.info("[refreshSmartGridActivity] 插入数据成功，mobile:{},activityId:{},gridId:{}", mobile, activityId, gridId);
//            }
//            if (!CollectionUtils.isEmpty(gridIds)) {
//                String join = String.join(",", gridIds);
//
//            }
//        }
        for (SweepFloorActivityDO sweepFloorActivityDO : sweepFloorActivityDOList) {
            List<String> gridIds = new ArrayList<>();
            for (GridUserRoleDetail gridUserRoleDetail : gridUserInfoResult.getData()) {
                Long activityId = sweepFloorActivityDO.getId();
                String gridId = gridUserRoleDetail.getId();
                gridIds.add(gridId);
                //判断记录是否存在
                SmartGridActivityQuery smartGridActivityQuery = new SmartGridActivityQuery();
                smartGridActivityQuery.setActivityId(activityId);
                smartGridActivityQuery.setGridId(gridId);
                SmartGridActivityDO old = smartGridActivityMapper.get(smartGridActivityQuery);
                if (old != null) {
                    log.info("[refreshSmartGridActivity] 数据已存在，mobile:{},activityId:{},gridId:{}", mobile, activityId, gridId);
                    continue;
                }

                SmartGridActivityDO smartGridActivityDO = new SmartGridActivityDO();
                smartGridActivityDO.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
                smartGridActivityDO.setActivityId(activityId);
                smartGridActivityDO.setGridId(gridId);
                smartGridActivityDO.setGridName(gridUserRoleDetail.getName());
                smartGridActivityMapper.insert(smartGridActivityDO);
                log.info("[refreshSmartGridActivity] 插入数据成功，mobile:{},activityId:{},gridId:{}", mobile, activityId, gridId);

            }
            if (!CollectionUtils.isEmpty(gridIds)) {
                String join = String.join(",", gridIds);
                sweepFloorActivityDO.setGridId(join);
                sweepFloorActivityMapper.update(sweepFloorActivityDO);
            }

        }

        log.info("[refreshSmartGridActivity] 更新数据完成，mobile:{}", mobile);
    }

    /**
     * 非网格用户刷新网格id
     * @param mobile
     * @param sweepFloorActivityDOList
     */
    private void refreshSmartGridIdWhenNotGridUser(String mobile,List<SweepFloorActivityDO> sweepFloorActivityDOList) {
        //该用户网格不存在
        for (SweepFloorActivityDO sweepFloorActivityDO : sweepFloorActivityDOList) {
            Long activityId = sweepFloorActivityDO.getId();
            String gridId = "0";
            //更新活动表
            sweepFloorActivityDO.setGridId(gridId);
            sweepFloorActivityMapper.update(sweepFloorActivityDO);
            //判断记录是否存在
            SmartGridActivityQuery smartGridActivityQuery = new SmartGridActivityQuery();
            smartGridActivityQuery.setActivityId(activityId);
            smartGridActivityQuery.setGridId(gridId);
            SmartGridActivityDO old = smartGridActivityMapper.get(smartGridActivityQuery);
            if (old != null) {
                log.info("[refreshSmartGridActivity] 数据已存在，mobile:{},activityId:{},gridId:{}", mobile, sweepFloorActivityDO.getId(),gridId);
                continue;
            }

            SmartGridActivityDO smartGridActivityDO = new SmartGridActivityDO();
            smartGridActivityDO.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
            smartGridActivityDO.setActivityId(activityId);
            smartGridActivityDO.setGridId(gridId);
            smartGridActivityDO.setGridName("无网格");
            smartGridActivityMapper.insert(smartGridActivityDO);
            log.info("[refreshSmartGridActivity] 插入数据成功，mobile:{},activityId:{},gridId:{}", mobile, activityId, gridId);
        }
    }

    @Override
    public ApiResult<ListVO<SmartGridVO>> getSweepFloorActivityByGridIds(List<String> gridIds, boolean page, Integer pageSize, Integer currentPage) {
        if (CollectionUtils.isEmpty(gridIds)) {
            ApiResult.of(0,ListVO.list(new ArrayList<>(),0));
        }
        SmartGridActivityQuery gridActivityQuery = new SmartGridActivityQuery();
        gridActivityQuery.setGridIds(gridIds);
        gridActivityQuery.setQueryTotal(page);
        gridActivityQuery.setPageSize(pageSize);
        gridActivityQuery.setCurrentPage(currentPage);
        gridActivityQuery.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
        List<SmartGridActivityDO> smartGridActivityDOS = smartGridActivityMapper.find(gridActivityQuery);
        if (CollectionUtils.isEmpty(smartGridActivityDOS)) {
            ApiResult.of(0,ListVO.list(new ArrayList<>(),0));
        }
        List<Long> activityIds = smartGridActivityDOS.stream().map(SmartGridActivityDO::getActivityId).collect(Collectors.toList()).
                stream().distinct().collect(Collectors.toList());
        SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
        activityQuery.setIds(activityIds);
        List<SweepFloorActivityDO> activityDOS = sweepFloorActivityMapper.find(activityQuery);
        if (CollectionUtils.isEmpty(activityDOS)) {
            return ApiResult.of(0,ListVO.list(new ArrayList<>(),0));
        }
        SmartGridActivityQuery countQuery = new SmartGridActivityQuery();
        countQuery.setGridIds(gridIds);
        countQuery.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
        long count = smartGridActivityMapper.count(countQuery);
        List<SmartGridVO> smartGridVOS = new ArrayList<>();
        Map<String, List<SmartGridActivityDO>> listMap = smartGridActivityDOS.stream().collect(Collectors.groupingBy(SmartGridActivityDO::getGridId));
        Iterator<String> iterator = listMap.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            List<SmartGridActivityDO> doList = listMap.get(next);

            SmartGridVO smartGridVO = new SmartGridVO();
            List<SweepFloorActivityVO> activityVOS = new ArrayList<>();
            for (SmartGridActivityDO smartGridActivityDO: doList) {
                for (SweepFloorActivityDO activityDO:activityDOS) {
                    SweepFloorActivityVO activityVO = new SweepFloorActivityVO();
                    BeanUtils.copyProperties(activityDO,activityVO);
                    if (smartGridActivityDO.getActivityId().equals(activityDO.getId())) {
                        activityVO.setGridId(smartGridActivityDO.getGridId());
                        activityVO.setGridName(smartGridActivityDO.getGridName());
                        activityVO.setCreatorName(activityDO.getCreatorName());
                        activityVO.setGmtCreate(activityDO.getGmtCreate());
                        activityVOS.add(activityVO);
                    }
                }
            }
            smartGridVO.setActivityVOList(activityVOS);
            smartGridVO.setId(doList.get(0).getGridId());
            smartGridVO.setName(doList.get(0).getGridName());
            smartGridVOS.add(smartGridVO);
        }
        return ApiResult.of(0,ListVO.list(smartGridVOS,count));
    }

    @Override
    public ApiResult<ListVO<SweepFloorActivityVO>> getOutsideActivityList(Integer status, Integer pageSize, Integer currentPage, Long startTime, Long endTime, String mobile, String gridId) {
        SweepFloorActivityQuery sweepFloorActivityQuery = new SweepFloorActivityQuery();
        sweepFloorActivityQuery.setStatus(status);
        if (startTime != null) {
            sweepFloorActivityQuery.setStartTime(new Date(startTime));
        }
        if (endTime != null) {
            sweepFloorActivityQuery.setEndTime(new Date(endTime));
        }
        sweepFloorActivityQuery.setGridId(gridId);
        sweepFloorActivityQuery.setQueryTotal(true);
        sweepFloorActivityQuery.setCurrentPage(currentPage);
        sweepFloorActivityQuery.setMobile(mobile);
        sweepFloorActivityQuery.setPageSize(pageSize);
        sweepFloorActivityQuery.setCreateTime(new Date());
        if (status != null && status == SweepFloorStatusEnum.END.getId()) {
            sweepFloorActivityQuery.setStatus(null);
            List<Integer> statusList = new ArrayList<>();
            statusList.add(SweepFloorStatusEnum.ABNORMAL_END.getId());
            statusList.add(SweepFloorStatusEnum.CANCEL.getId());
            statusList.add(SweepFloorStatusEnum.END.getId());
            sweepFloorActivityQuery.setStatusList(statusList);
        }
        List<SweepFloorActivityDO> sweepFloorActivityDOS = sweepFloorActivityMapper.find(sweepFloorActivityQuery);
        if (CollectionUtils.isEmpty(sweepFloorActivityDOS)) {
            return ApiResult.of(0,ListVO.list(new ArrayList<>(),0));
        }
        List<SweepFloorActivityVO> activityVOS = new ArrayList<>();
        for (SweepFloorActivityDO activityDO:sweepFloorActivityDOS) {
            SweepFloorActivityVO activityVO = new SweepFloorActivityVO();
            BeanUtils.copyProperties(activityDO,activityVO);
            if (activityVO.getStatus() == SweepFloorStatusEnum.CANCEL.getId()) {
                activityVO.setStatus(SweepFloorStatusEnum.ABNORMAL_END.getId());
            }
            activityVOS.add(activityVO);
        }
        long count = sweepFloorActivityMapper.count(sweepFloorActivityQuery);

        return ApiResult.of(0,ListVO.list(activityVOS,count));
    }

    @Override
    public ApiResult<SweepFloorActivityVO> getOutsideActivityDetail(Long id) {
        SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
        activityQuery.setId(id);
        SweepFloorActivityDO activityDO = sweepFloorActivityMapper.get(activityQuery);
        if (activityDO == null) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_ACTIVITY_NOT_EXIST);
        }
        SweepFloorActivityVO activityVO = new SweepFloorActivityVO();
        BeanUtils.copyProperties(activityDO,activityVO);
        if (activityVO.getStatus() == SweepFloorStatusEnum.ABNORMAL_END.getId()) {
            activityVO.setAbnormalReason(SweepFloorStatusEnum.ABNORMAL_END.getDesc());
        }else if (activityVO.getStatus() == SweepFloorStatusEnum.CANCEL.getId()) {
            activityVO.setAbnormalReason(SweepFloorStatusEnum.CANCEL.getDesc());
            activityVO.setStatus(SweepFloorStatusEnum.ABNORMAL_END.getId());
        }
        return ApiResult.of(0,activityVO);
    }

    private void householdVOToHuawei(HuaweiTenantRequest huaweiRequest, HouseholdVO request) {
        huaweiRequest.setBroadbandFlag(request.getBroadbandFlag());
        huaweiRequest.setBuildingId(request.getBuildingId());
        huaweiRequest.setBuildingName(request.getBuildingName());
        huaweiRequest.setCellId(request.getCommunityId());
        huaweiRequest.setHouseNumber(request.getHouseNumber());

        if (CollectionUtils.isEmpty(request.getFamilyMembers())) {
            huaweiRequest.setFamilyMembers(new ArrayList<>());
        } else {
            huaweiRequest.setFamilyMembers(request.getFamilyMembers());
        }


        if (!CollectionUtils.isEmpty(request.getServiceProvider())) {
            List<String> types = request.getServiceProvider();
            String type = "";
            for (int i = 0;i < types.size();i++) {
                String s = types.get(i);
                if (i < types.size() -1) {
                    type += s + ",";
                }else {
                    type += s;
                }
            }
            huaweiRequest.setServiceProvider(type);
        }
//        if (request.getTVBoxTypes() != null) {
//            huaweiRequest.setTVBoxTypes(String.join(",", request.getTVBoxTypes()));
//        }
//        huaweiRequest.setBroadbandRemark(request.getBroadbandRemark());
//        huaweiRequest.setBroadbandMonthlyrent(request.getBroadbandMonthlyrent());
//        huaweiRequest.setTVBoxRemark(request.getTVBoxRemark());
//        if (request.getTVBoxExpireTime() != null) {
//            huaweiRequest.setTVBoxExpireTime(DateUtils.dateToString(request.getTVBoxExpireTime(), "yyyy-MM-dd"));
//        }
        huaweiRequest.setUnitId(request.getUnitId());
        huaweiRequest.setBroadbandExpireTime(DateUtils.dateToString(request.getBroadbandExpireTime(), "yyyy-MM-dd"));
    }

    /**
     * 对象转换
     *
     * @param huaweiRequest
     * @param request
     */
    private void buildingVoToHuawei(HuaweiBuildingRequest huaweiRequest, BuildingVO request) {

        if (!CollectionUtils.isEmpty(request.getBroadbandType())) {
            List<String> types = request.getBroadbandType();
            String type = "";
            for (int i = 0;i < types.size();i++) {
                String s = types.get(i);
                if (i < types.size() -1) {
                    type += s + ",";
                }else {
                    type += s;
                }
            }
            huaweiRequest.setBroadbandType(type);
        }
        huaweiRequest.setBroadbandUserCount(request.getBroadbandUserCount());
        huaweiRequest.setBuildingId(request.getBuildingId());
        huaweiRequest.setBuildingName(request.getBuildingName());
        huaweiRequest.setCellId(request.getCommunityId());
        huaweiRequest.setFloorNumber(request.getFloorNumber());
        huaweiRequest.setHouseholderCount(request.getHouseholderCountEveryFloor());
        huaweiRequest.setRemainingPortCount(request.getRemainingPortCount());
        huaweiRequest.setUnitCount(request.getUnitCount());
    }

    /**
     * 检验打卡范围
     *
     * @param dbLocation
     * @param reqLocation
     * @return
     */
    private ApiResult checkDistanceWhenSign(String dbLocation, String reqLocation) {
        String[] dbSplit = dbLocation.split(",");
        String[] reqSplit = reqLocation.split(",");
        double Lat1 = Double.parseDouble(dbSplit[1]);
        double Lon1 = Double.parseDouble(dbSplit[0]);
        double Lat2 = Double.parseDouble(reqSplit[1]);
        double Lon2 = Double.parseDouble(reqSplit[0]);
        double distance = DistanceUtils.getDistanceFromCoordinates(Lat1, Lon1, Lat2, Lon2);
        if (distance > stallUpConfig.getConfig().getRadius()) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_SIGN_DISTANCE_ERROR);
        }
        return ApiResult.of(0);
    }

    private SignRecordDO getSignRecord(Long activityId) {
        SignRecordQuery signRecordQuery = new SignRecordQuery();
        signRecordQuery.setActivityId(activityId);
        signRecordQuery.setBizType(SignRecordBizTypeEnum.SWEEP_FLOOR.getId());
        SignRecordDO signRecordDO = signRecordMapper.get(signRecordQuery);
        return signRecordDO;
    }

    /**
     * 更新扫楼活动状态
     */
    private void updateActivityStatus(Long id, Integer status,Date startTime,Date endTime,boolean flag) {
        SweepFloorActivityDO activityDO = new SweepFloorActivityDO();
        activityDO.setId(id);
        activityDO.setStatus(status);
        activityDO.setStartTime(startTime);
        activityDO.setEndTime(endTime);
        if (!flag) {
            activityDO.setStatus(SweepFloorStatusEnum.ABNORMAL_END.getId());
        }
        sweepFloorActivityMapper.update(activityDO);
    }

    /**
     * 校验活动状态
     *
     * @param id
     * @return
     */
    private ApiResult checkActivityStatusWhenStartSign(Long id) {
        SweepFloorActivityQuery activityQuery = new SweepFloorActivityQuery();
        //activityQuery.setCreator(UserLoginHelper.getUid());
        activityQuery.setCreator(SmartGridContext.getUid());
        List<SweepFloorActivityDO> activityDOS = sweepFloorActivityMapper.find(activityQuery);
        Map<Long, Integer> activityMap = activityDOS.stream().collect(Collectors.toMap(SweepFloorActivityDO::getId, SweepFloorActivityDO::getStatus));
        //校验当前活动是否是为开始
        Integer activityStatus = activityMap.get(id);
        if (activityStatus != SweepFloorStatusEnum.NOT_START.getId()) {
            log.error("satrtSign error status error ,activityId = {},status = {}", id, activityStatus);
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_STATUS_ERROR_START_SIGN);
        }
        //校验是否存在进行中的扫楼
        Iterator<Long> iterator = activityMap.keySet().iterator();
        while (iterator.hasNext()) {
            Long next = iterator.next();
            Integer status = activityMap.get(next);
            if (!next.equals(id) && status == SweepFloorStatusEnum.PROCESSING.getId()) {
                log.error("satrtSign error processing activity already exist,activityId = {}", next);
                return ApiResultWrapper.fail(SweepFloorErrorCodes.EXIST_PROCESSING_SWEEP_FLOOR);
            }
        }
        return null;
    }


    /**
     * 根据活动id校验活动是否存在
     *
     * @param id
     * @return
     */
    private ApiResult checkActivityExist(Long id) {
        //todo 校验人员信息和活动id
        SweepFloorActivityQuery query = new SweepFloorActivityQuery();
        query.setId(id);
        SweepFloorActivityDO dbActivity = sweepFloorActivityMapper.get(query);
        if (dbActivity == null) {
            return ApiResultWrapper.fail(SweepFloorErrorCodes.SWEEP_FLOOR_ACTIVITY_NOT_EXIST);
        }
        return ApiResult.of(0, dbActivity);
    }


}
