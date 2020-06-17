package com.shinemo.wangge.core.service.stallup.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.reflect.TypeToken;
import com.shinemo.client.util.DateUtil;
import com.shinemo.client.util.GsonUtil;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.domain.LockContext;
import com.shinemo.my.redis.service.RedisLock;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.domain.model.UserConfigDO;
import com.shinemo.smartgrid.domain.query.UserConfigQuery;
import com.shinemo.smartgrid.utils.AESUtil;
import com.shinemo.smartgrid.utils.DateUtils;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.common.enums.HuaweiStallUpUrlEnum;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import com.shinemo.stallup.domain.enums.StallUpExceptionEnum;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.huawei.GetGridUserInfoResult;
import com.shinemo.stallup.domain.model.*;
import com.shinemo.stallup.domain.query.ParentStallUpActivityQuery;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;
import com.shinemo.stallup.domain.query.StallUpMarketingNumberQuery;
import com.shinemo.stallup.domain.request.*;
import com.shinemo.stallup.domain.response.*;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.stallup.domain.utils.LocalDateTimeUtils;
import com.shinemo.sweepfloor.domain.model.SignRecordDO;
import com.shinemo.sweepfloor.domain.query.SignRecordQuery;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.enums.TodoMethodOperateEnum;
import com.shinemo.todo.enums.TodoStatusEnum;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.todo.vo.TodoThirdRequest;
import com.shinemo.wangge.core.config.StallUpConfig;
import com.shinemo.wangge.core.service.stallup.HuaWeiService;
import com.shinemo.wangge.core.service.stallup.StallUpService;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingService;
import com.shinemo.wangge.core.service.todo.TodoService;
import com.shinemo.wangge.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 智慧网格 摆摊活动服务
 *
 * @author Chenzhe Mao
 * @date 2020-04-02
 */
@Service("stallUpService")
@Slf4j
public class StallUpServiceImpl implements StallUpService {

    @Value("${smartgrid.huawei.aesKey}")
    public String aeskey;

    private static final Integer BIZ_TYPE = 2;
    private static final Long OTHER_ID = 1L;
    private static final Integer EXPIRE_TIME = 5;
    private static final Integer INTERVAL_TIME = 1;
    private static final String CREATE_KEY = "cmgr_stallup_c_mobile_%s_id_%s";
    // 更新父摆摊，粒度为父摆摊id
    private static final String UPDATE_PARENT_KEY = "cmgr_stallup_up_pid_%s";
    private static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final Function<Object, String> toJson = v -> v == null ? null : GsonUtils.toJson(v);
    private static final Function<Date, String> date2String = v -> v == null ? null : FORMAT.format(v);
    private static final BiFunction<Long, Long, Boolean> timeFilter = (a, b) -> a != null && b != null
            && a.longValue() >= b.longValue();

    private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final TypeToken<List<StallUpBizType>> BIZ_DETAIL_TYPE = new TypeToken<List<StallUpBizType>>() {
    };

    @Resource
    private StallUpActivityMapper stallUpActivityMapper;

    @Resource
    private SignRecordMapper signRecordMapper;

    @Resource
    private UserConfigMapper userConfigMapper;

    @Resource
    private StallUpMarketingNumberMapper stallUpMarketingNumberMapper;

    @Resource
    private StallUpConfig stallUpConfig;

    @Resource
    private RedisLock redisLock;

    @Resource
    private ParentStallUpActivityMapper parentStallUpActivityMapper;

    @Resource
    private HuaWeiService huaWeiService;

    @NacosValue(value = "${wangge.index.default.biz}", autoRefreshed = true)
    private String defaultBiz;

    private static final String STALL_UP_ID_PREFIX = "SL_";

    @Resource
    private ThirdApiMappingService thirdApiMappingService;
    private static final String ID_PREFIX = "BT_";

    @Resource
    private TodoService todoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(StallUpRequest stallUpRequest) {
        String key = String.format(CREATE_KEY, stallUpRequest.getMobile(), stallUpRequest.getId());
        boolean hasLock = redisLock.lock(LockContext.create(key, INTERVAL_TIME));
        if (!hasLock) {
            log.error("[create] redisLock.tryLock failed, key:{}", key);
            throw new ApiException(StallUpErrorCodes.FREQUENT_ERROR);
        }
        StallUpCreateRequest request = (StallUpCreateRequest) stallUpRequest;
        List<Long> bizTypeList = request.getBizTypeList();
        if (bizTypeList == null) {
            bizTypeList = new ArrayList<>();
        }
        if (!bizTypeList.contains(OTHER_ID)) {
            bizTypeList.add(OTHER_ID);
        }
        List<GridUserDetail> partnerList = request.getPartnerList();
        if (CollectionUtils.isEmpty(partnerList)) {
            log.error("[create] request.getPartnerList is empty, request:{}", request);
            throw new ApiException(StallUpErrorCodes.PARTNER_EMPTY);
        }
        List<StallUpActivity> insertList = new ArrayList<>();
        Date startTime = new Date(request.getStartTime());
        Date endTime = new Date(request.getEndTime());
        String custIdListJson = toJson.apply(request.getCustList());
        String bizTypeListJson = toJson.apply(request.getBizTypeList());
        Map<String, String> nameMap = new HashMap<>();
        List<GridUserDetail> list = partnerList.stream().filter(v -> {
            String mobile = AESUtil.decrypt(v.getSeMobile(), aeskey);
            if (mobile == null) {
                log.error("[create] AESUtil.decrypt mobile error, request:{}, mobile:{}", request, v.getSeMobile());
                return false;
            }
            v.setMobile(mobile);
            nameMap.put(mobile, v.getName());
            return true;
        }).collect(Collectors.toList());
        String partnerListJson = toJson.apply(list);

        for (GridUserDetail detail : list) {
            StallUpActivity stallUpActivity = StallUpActivity.builder().communityId(request.getCommunityId())
                    .communityName(request.getCommunityName()).address(request.getAddress()).creatorId(request.getUid())
                    .mobile(detail.getMobile()).creatorOrgId(request.getOrgId()).title(request.getTitle())
                    .startTime(startTime).endTime(endTime).location(request.getLocation()).partner(partnerListJson)
                    .gridId(request.getGridId()).custIdList(custIdListJson).bizList(bizTypeListJson)
                    .status(StallUpStatusEnum.PREPARE.getId()).name(nameMap.get(detail.getMobile())).build();
            insertList.add(stallUpActivity);
        }
        if (insertList.size() > 0) {
            // 插入父摆摊
            ParentStallUpActivity parent = new ParentStallUpActivity();
            BeanUtils.copyProperties(insertList.get(0), parent);
            parent.setId(null);
            parent.setMobile(request.getMobile());
            String name = null;
            ApiResult<GetGridUserInfoResult.DataBean> result = null;
            try {
                result = huaWeiService
                        .getGridUserInfoDetail(HuaWeiRequest.builder().mobile(request.getMobile()).build());
                if (result.isSuccess()) {
                    name = result.getData().getUserName();
                }
            } catch (ApiException e) {
                log.error("[create] failed call huaWeiService.getGridUserInfoDetail, mobile:{}, result:{}",
                        request.getMobile(), result);
            }
            if (name == null) {
                name = SmartGridContext.getUserName();
            }
            parent.setName(name);

            parent.setFlag(0);
            parentStallUpActivityMapper.insert(parent);
            // 批量插入子摆摊
            stallUpActivityMapper.insertBatch(
                    insertList.stream().peek(v -> v.setParentId(parent.getId())).collect(Collectors.toList()));

            Map<String, Object> map = new HashMap<>();
            map.put("id", STALL_UP_ID_PREFIX + parent.getId());
            map.put("gmtCreate", DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            map.put("communityName", parent.getCommunityName());
            map.put("communityId", parent.getCommunityId());
            map.put("address", parent.getAddress());
            map.put("location", parent.getLocation());
            map.put("mobile", parent.getMobile());
            map.put("name", parent.getName());
            map.put("title", parent.getTitle());
            map.put("startTime", DateUtils.dateToString(parent.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("endTime", DateUtils.dateToString(parent.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("status", StallUpStatusEnum.PREPARE.getId());
            map.put("gridId", parent.getGridId());
            List<GridUserDetailSimple> simpleList = new ArrayList<>(partnerList.size());
            for (GridUserDetail detail: partnerList) {
                GridUserDetailSimple detailSimple = GridUserDetailSimple.builder().mobile(detail.getMobile()).
                        name(detail.getName()).role(detail.getRole()).build();
                simpleList.add(detailSimple);
            }
            map.put("partners", simpleList);
            map.put("custIdList", request.getCustList());
            thirdApiMappingService.dispatch(map, HuaweiStallUpUrlEnum.SYNCHRONIZE_STALL_DATA.getMethod());


            //同步代办事项
            for (StallUpActivity stallUpActivity : insertList) {
                syncTodoCreate(stallUpActivity);
            }
        }
    }

    private void syncTodoCreate(StallUpActivity stallUpActivity) {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setThirdId(String.valueOf(stallUpActivity.getId()));
        todoDTO.setThirdType(ThirdTodoTypeEnum.BAI_TAN_PLAN.getId());
        todoDTO.setOperateType(TodoMethodOperateEnum.CREATE.getId());
        todoDTO.setTitle(stallUpActivity.getTitle());
        todoDTO.setRemark(stallUpActivity.getAddress());
        todoDTO.setStatus(TodoStatusEnum.NOT_FINISH.getId());
        todoDTO.setLabel(StallUpStatusEnum.PREPARE.getName());
        todoDTO.setOperatorMobile(stallUpActivity.getMobile());
        todoDTO.setOperatorTime(DateUtil.format(stallUpActivity.getEndTime()));
        todoDTO.setStartTime(DateUtil.format(stallUpActivity.getStartTime()));
        ApiResult<TodoThirdRequest> todoRequest = todoService.getTodoThirdRequest(todoDTO);
        todoService.operateTodoThing(todoRequest.getData());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(StallUpRequest stallUpRequest) {
        StallUpCancelRequest request = (StallUpCancelRequest) stallUpRequest;
        int status = StallUpStatusEnum.CANCELED.getId();
        StallUpActivity activity = StallUpActivity.builder().id(request.getId()).status(status).build();
        stallUpActivityMapper.update(activity);
        StallUpActivity child = request.getActivity();
        child.setStatus(status);
        String key = String.format(UPDATE_PARENT_KEY, activity.getParentId());
        boolean hasLock = redisLock.lock(LockContext.create(key, EXPIRE_TIME));
        if (!hasLock) {
            log.error("[cancel] redisLock.tryLock failed, key:{}", key);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        try {
            updateParent(child, null, new Date());

            //同步代办事项
            syncTodoUpdate(status, request.getId());

        } finally {
            redisLock.unlock(LockContext.create(key));
        }
    }

    private void syncTodoUpdate(int status, Long id) {
        StallUpActivity stallUp = getStallUp(id);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setThirdId(String.valueOf(id));
        todoDTO.setThirdType(ThirdTodoTypeEnum.BAI_TAN_PLAN.getId());
        todoDTO.setOperateType(TodoMethodOperateEnum.UPDATE.getId());
        todoDTO.setStatus(TodoStatusEnum.OTHER.getId());
        todoDTO.setLabel(StallUpStatusEnum.getById(status).getName());
        todoDTO.setOperatorMobile(stallUp.getMobile());
        ApiResult<TodoThirdRequest> todoRequest = todoService.getTodoThirdRequest(todoDTO);
        todoService.operateTodoThing(todoRequest.getData());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sign(StallUpRequest stallUpRequest) {
        StallUpSignRequest request = (StallUpSignRequest) stallUpRequest;
        int status = StallUpStatusEnum.STARTED.getId();
        // 是否有正在进行的摆摊计划
        StallUpActivityQuery query = new StallUpActivityQuery();
        query.setMobile(request.getMobile());
        query.setStatus(status);
        StallUpActivity stallUpActivity = stallUpActivityMapper.get(query);
        if (stallUpActivity != null) {
            log.error("[sign] exist started, stallUpActivity:{}", stallUpActivity);
            throw new ApiException(StallUpErrorCodes.SIGN_ERROR);
        }
        // 插入签到记录
        Date startTime = new Date();
        SignRecordDO signRecordDO = new SignRecordDO();
        signRecordDO.setBizType(BIZ_TYPE);
        signRecordDO.setActivityId(request.getId());
        signRecordDO.setUserId(request.getUid().toString());
        signRecordDO.setStartTime(startTime);
        signRecordDO.setStartLocation(GsonUtils.toJson(
                StallUpSignDetail.builder().address(request.getAddress()).location(request.getLocation()).build()));
        signRecordMapper.insert(signRecordDO);
        // 更新摆摊计划状态
        StallUpActivity activity = StallUpActivity.builder().id(request.getId()).status(status).build();
        stallUpActivityMapper.update(activity);
        StallUpActivity child = request.getActivity();
        child.setStatus(status);
        String key = String.format(UPDATE_PARENT_KEY, child.getParentId());
        boolean hasLock = redisLock.lock(LockContext.create(key, EXPIRE_TIME));
        if (!hasLock) {
            log.error("[sign] redisLock.tryLock failed, key:{}", key);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        try {
            updateParent(child, startTime, null);
            //同步代办事项
            syncTodoUpdate(status, request.getId());

        } finally {
            redisLock.unlock(LockContext.create(key));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void end(StallUpRequest stallUpRequest) {
        StallUpEndRequest request = (StallUpEndRequest) stallUpRequest;
        SignRecordQuery query = new SignRecordQuery();
        query.setActivityId(request.getId());
        query.setBizType(BIZ_TYPE);
        SignRecordDO signRecordDO = signRecordMapper.get(query);
        if (signRecordDO == null) {
            log.error("[end] sign record not exist, request:{}", request);
            throw new ApiException(StallUpErrorCodes.SIGN_RECORD_NOT_EXIST);
        }
        StallUpActivity activity = request.getActivity();
        String location = request.getLocation();
        int distance = DistanceUtils.getDistance(location, activity.getLocation());
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        int status = StallUpStatusEnum.END.getId();
        if (distance > config.getRadius()) {
            log.error("[end] distance error, signLocation:{}, planLocation:{}, distance:{}", location,
                    activity.getLocation(), distance);
            status = StallUpStatusEnum.ABNORMAL_END.getId();
        }
        // 更新打卡记录
        Date endTime = new Date();
        SignRecordDO newSignRecordDO = new SignRecordDO();
        newSignRecordDO.setId(signRecordDO.getId());
        newSignRecordDO.setEndTime(endTime);
        newSignRecordDO.setEndLocation(GsonUtils.toJson(
                StallUpSignDetail.builder().address(request.getAddress()).location(request.getLocation()).build()));
        newSignRecordDO.setRemark(request.getRemark());
        newSignRecordDO.setImgUrl(GsonUtils.toJson(request.getImageList()));
        signRecordMapper.update(newSignRecordDO);
        // 更新摆摊计划状态
        StallUpActivity updateActivity = StallUpActivity.builder().id(request.getId()).status(status).build();
        stallUpActivityMapper.update(updateActivity);
        // 插入或更新摆摊办理量详情
        StallUpMarketingNumberQuery marketingNumberQuery = new StallUpMarketingNumberQuery();
        marketingNumberQuery.setActivityId(request.getId());
        StallUpMarketingNumber stallUpMarketingNumber = stallUpMarketingNumberMapper.get(marketingNumberQuery);
        if (stallUpMarketingNumber == null) {
            StallUpMarketingNumber value = StallUpMarketingNumber.builder().userId(request.getUid())
                    .activityId(request.getId()).remark(request.getBizRemark()).build();
            if (CollectionUtils.isEmpty(request.getBizDetailList())) {
                String bizList = activity.getBizList();
                List<Long> list = GsonUtils.fromGson2Obj(bizList, new TypeToken<List<Long>>() {
                }.getType());
                value.setCount(0);
                value.setDetail(GsonUtils.toJson(list.stream().map(v -> {
                    StallUpBizType bizType = new StallUpBizType();
                    bizType.setId(v);
                    bizType.setNum(0);
                    return bizType;
                }).collect(Collectors.toList())));
            } else {
                value.setCount(request.getBizDetailList().stream().mapToInt(v -> v.getNum()).sum());
                value.setDetail(toJson.apply(request.getBizDetailList()));
            }
            // 插入
            stallUpMarketingNumberMapper.insert(value);
        } else {
            // 更新
            if (!CollectionUtils.isEmpty(request.getBizDetailList())) {
                stallUpMarketingNumberMapper.update(StallUpMarketingNumber.builder().id(stallUpMarketingNumber.getId())
                        .count(request.getBizDetailList().stream().mapToInt(v -> v.getNum()).sum())
                        .detail(toJson.apply(request.getBizDetailList())).remark(request.getBizRemark()).build());
            }
        }
        StallUpActivity child = request.getActivity();
        child.setStatus(status);
        String key = String.format(UPDATE_PARENT_KEY, child.getParentId());
        boolean hasLock = redisLock.lock(LockContext.create(key, EXPIRE_TIME));
        if (!hasLock) {
            log.error("[end] redisLock.tryLock failed, key:{}", key);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        try {
            updateParent(child, null, endTime);

            //同步代办事项
            syncTodoUpdate(status, request.getId());
        } finally {
            redisLock.unlock(LockContext.create(key));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoEnd(StallUpRequest stallUpRequest) {
        StallUpEndRequest request = (StallUpEndRequest) stallUpRequest;
        int status = StallUpStatusEnum.AUTO_END.getId();
        SignRecordQuery query = new SignRecordQuery();
        query.setActivityId(request.getId());
        query.setBizType(BIZ_TYPE);
        SignRecordDO signRecordDO = signRecordMapper.get(query);
        if (signRecordDO == null) {
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        // 更新打卡记录
        Date endTime = new Date();
        SignRecordDO newSignRecordDO = new SignRecordDO();
        newSignRecordDO.setId(signRecordDO.getId());
        newSignRecordDO.setEndTime(endTime);
        signRecordMapper.update(newSignRecordDO);
        // 更新摆摊计划状态
        StallUpActivity updateActivity = StallUpActivity.builder().id(request.getId()).status(status).build();
        stallUpActivityMapper.update(updateActivity);
        // 插入或更新摆摊办理量详情
        StallUpMarketingNumberQuery marketingNumberQuery = new StallUpMarketingNumberQuery();
        marketingNumberQuery.setActivityId(request.getId());
        StallUpMarketingNumber stallUpMarketingNumber = stallUpMarketingNumberMapper.get(marketingNumberQuery);
        if (stallUpMarketingNumber == null) {
            StallUpActivityQuery stallUpActivityQuery = new StallUpActivityQuery();
            stallUpActivityQuery.setId(request.getId());
            StallUpActivity stallUpActivity = stallUpActivityMapper.get(stallUpActivityQuery);
            String bizList = stallUpActivity.getBizList();
            List<Long> list = GsonUtils.fromGson2Obj(bizList, new TypeToken<List<Long>>() {
            }.getType());

            // 插入
            stallUpMarketingNumberMapper.insert(StallUpMarketingNumber.builder().userId(request.getUid())
                    .activityId(request.getId()).count(0).detail(GsonUtils.toJson(list.stream().map(v -> {
                        StallUpBizType bizType = new StallUpBizType();
                        bizType.setId(v);
                        bizType.setNum(0);
                        return bizType;
                    }).collect(Collectors.toList()))).build());
        }
        StallUpActivity child = request.getActivity();
        child.setStatus(status);
        String key = String.format(UPDATE_PARENT_KEY, child.getParentId());
        boolean hasLock = redisLock.lock(LockContext.create(key, EXPIRE_TIME));
        if (!hasLock) {
            log.error("[autoEnd] redisLock.tryLock failed, key:{}", key);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        try {
            updateParent(child, null, endTime);

            syncTodoUpdate(status, request.getId());
        } finally {
            redisLock.unlock(LockContext.create(key));
        }
    }

    @Override
    public StallUpActivity getStallUp(Long id) {
        StallUpActivityQuery query = new StallUpActivityQuery();
        query.setId(id);
        return stallUpActivityMapper.get(query);
    }

    @Override
    public ApiResult<GetDetailResponse> getStallUpActivity(Long id, String mobile) {
        ApiResult<StallUpActivity> result = check(id, mobile);
        if (!result.isSuccess()) {
            log.error("[getDetail] id, mobile not match, id:{}, mobile:{}", id, mobile);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        StallUpActivity stallUpActivity = result.getData();
        // 业务办理
        List<Long> bizIdList = GsonUtils.fromGson2Obj(stallUpActivity.getBizList(), new TypeToken<List<Long>>() {
        }.getType());
        List<StallUpBizType> bizTypeList = new ArrayList<>();
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        Map<Long, StallUpBizType> bizMap = config.getBizMap();
        if (bizIdList != null && bizIdList.size() > 0) {
            bizIdList.forEach(v -> {
                StallUpBizType stallUpBizType = bizMap.get(v);
                StallUpBizType newBizType = new StallUpBizType();
                BeanUtils.copyProperties(stallUpBizType, newBizType);
                newBizType.setNum(0);
                bizTypeList.add(newBizType);
            });
            Collections.sort(bizTypeList);
        }
        StallUpMarketingNumberQuery marketingNumberQuery = new StallUpMarketingNumberQuery();
        marketingNumberQuery.setActivityId(id);
        StallUpMarketingNumber stallUpMarketingNumber = stallUpMarketingNumberMapper.get(marketingNumberQuery);
        GetDetailResponse response = new GetDetailResponse();
        response.setTitle(stallUpActivity.getTitle());
        response.setAddress(stallUpActivity.getAddress());
        response.setBizList(bizTypeList);
        if (stallUpMarketingNumber != null) {
            response.setBizRemark(stallUpMarketingNumber.getRemark());
            String detail = stallUpMarketingNumber.getDetail();
            if (StringUtils.hasText(detail)) {
                List<StallUpBizDetail> list = GsonUtils.fromGson2Obj(detail, new TypeToken<List<StallUpBizDetail>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    Map<Long, Integer> map = new HashMap<>();
                    list.forEach(v -> map.put(v.getId(), v.getNum()));
                    response.setBizList(bizTypeList.stream().map(v -> {
                        v.setNum(map.get(v.getId()));
                        return v;
                    }).collect(Collectors.toList()));
                }
            }
        }
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<GetEndListResponse> getEndList(String mobile, Integer page, Integer pageSize, Long startTime,
                                                    Long endTime) {
        Page result = PageHelper.startPage(page, pageSize, true);
        // 实际结束时间倒序
        PageHelper.orderBy("real_end_time desc");
        // 查询已结束和自动结束的摆摊
        StallUpActivityQuery query = new StallUpActivityQuery();
        query.setMobile(mobile);
        query.setStatusList(Arrays.asList(StallUpStatusEnum.END.getId(), StallUpStatusEnum.AUTO_END.getId(),
                StallUpStatusEnum.ABNORMAL_END.getId()));
        query.setBizType(BIZ_TYPE);
        if (startTime != null) {
            query.setGtRealEndTime(new Date(startTime));
        }
        if (endTime != null) {
            query.setLtRealEndTime(new Date(endTime));
        }
        GetEndListResponse response = new GetEndListResponse();
        List<StallUpDetail> endList = stallUpActivityMapper.getList(query);
        if (endList != null || endList.size() > 0) {
            response.setEndList(endList.stream().map(v -> getEndDetail(v)).collect(Collectors.toList()));
        }
        response.setTotal(result.getTotal());
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<GetStallUpListResponse> getList(String mobile) {
        LocalDateTime now = LocalDateTime.now();
        // 是当周第几天
        int dayOfWeek = now.getDayOfWeek().getValue();
        // 是当月第几天
        int dayOfMonth = now.getDayOfMonth();
        // 本周第一天可能小于本月第一天
        int maxBeforeDays = Math.max(dayOfMonth, dayOfWeek);
        // 查询当前用户全量摆摊记录
        StallUpActivityQuery query = new StallUpActivityQuery();
        query.setMobile(mobile);
        query.setBizType(BIZ_TYPE);
        query.setStatusList(Arrays.asList(StallUpStatusEnum.PREPARE.getId(), StallUpStatusEnum.STARTED.getId()));
        List<StallUpDetail> list = stallUpActivityMapper.getList(query);
        List<StallUpDetail> startList = new ArrayList<>();
        List<StallUpDetail> prepareList = new ArrayList<>();
        list.forEach(v -> {
            // 待开始
            if (v.getStatus().equals(StallUpStatusEnum.PREPARE.getId())) {
                prepareList.add(v);
            }
            // 已开始
            else if (v.getStatus().equals(StallUpStatusEnum.STARTED.getId()) && startList.size() == 0) {
                startList.add(v);
            }
        });
        // 查询当前用户 状态是已结束的摆摊
        query.setStatusList(Arrays.asList(StallUpStatusEnum.END.getId(), StallUpStatusEnum.AUTO_END.getId(),
                StallUpStatusEnum.ABNORMAL_END.getId()));
        // 本周和本月已完成的一起load到内存中再过滤
        query.setRealEndDays(maxBeforeDays - 1);
        List<StallUpDetail> endList = stallUpActivityMapper.getList(query);
        GetStallUpListResponse response = new GetStallUpListResponse();
        // 本周已结束统计
        Long nowMillions = LocalDateTimeUtils.toEpochMilli(now);
        // 本周第一天
        LocalDateTime firstDayOfWeek = LocalDateTimeUtils.getFirstDayOfWeek(now);
        // 本周第一天的时间戳
        Long firstDayOfWeekMillions = LocalDateTimeUtils.getStartOfDay(firstDayOfWeek);
        // 本周第一天 <= 实际结束时间 <= 当前时间
        List<StallUpDetail> weekList = endList.stream()
                .filter(v -> timeFilter.apply(v.getRealEndTime().getTime(), firstDayOfWeekMillions)
                        && timeFilter.apply(nowMillions, v.getRealEndTime().getTime()))
                .collect(Collectors.toList());
        response.setWeekDetail(StallUpBizTotal.builder().stallUpNum(weekList.size())
                .bizNum(weekList.stream().mapToInt(v -> v.getBizTotal()).sum()).build());
        // 本月已结束统计
        // 本月第一天
        LocalDateTime firstDayOfMonth = LocalDateTimeUtils.getFirstDayOfMonth(now);
        // 本月第一天的时间戳
        Long firstDayOfMonthMillions = LocalDateTimeUtils.getStartOfDay(firstDayOfMonth);
        // 本月第一天 <= 实际结束时间 <= 当前时间
        List<StallUpDetail> monthList = endList.stream()
                .filter(v -> timeFilter.apply(v.getRealEndTime().getTime(), firstDayOfMonthMillions)
                        && timeFilter.apply(nowMillions, v.getRealEndTime().getTime()))
                .collect(Collectors.toList());
        response.setMonthDetail(StallUpBizTotal.builder().stallUpNum(monthList.size())
                .bizNum(monthList.stream().mapToInt(v -> v.getBizTotal()).sum()).build());
        // 已开始，最多一条
        if (startList.size() > 0) {
            response.setStartedDetail(getDetailVO(startList.get(0)));
        }
        // 待开始计划开始时间升序
        if (prepareList.size() > 0) {
            response.setPrepareDetail(prepareList.stream().map(v -> getDetailVO(v))
                    .sorted(Comparator.comparing(StallUpDetailVO::getStartTime)).collect(Collectors.toList()));
        } else {
            response.setPrepareDetail(new ArrayList<>());
        }
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<GetStallUpSimpleInfoResponse> getSimpleInfo(String mobile) {
        LocalDateTime now = LocalDateTime.now();
        int dayOfMonth = now.getDayOfMonth();
        int dayOfWeek = now.getDayOfWeek().getValue();
        // 下周一0点
        Long startOfNextWeek = LocalDateTimeUtils.getStartOfDay(now.plusDays(8 - dayOfWeek));
        // 联表查询
        StallUpActivityQuery query = new StallUpActivityQuery();
        query.setMobile(mobile);
        query.setStatusList(Arrays.asList(StallUpStatusEnum.STARTED.getId()));
        query.setBizType(BIZ_TYPE);
        // 已开始
        List<StallUpDetail> startList = stallUpActivityMapper.getList(query);

        query.setStatusList(Arrays.asList(StallUpStatusEnum.PREPARE.getId()));
        query.setStartOfNextWeek(new Date(startOfNextWeek));
        // 本周待开始列表
        List<StallUpDetail> prepareList = stallUpActivityMapper.getList(query);

        query.setStatusList(Arrays.asList(StallUpStatusEnum.END.getId(), StallUpStatusEnum.AUTO_END.getId(),
                StallUpStatusEnum.ABNORMAL_END.getId()));
        query.setStartOfNextWeek(null);
        query.setRealEndDays(dayOfMonth - 1);
        // 本月已结束列表
        List<StallUpDetail> endList = stallUpActivityMapper.getList(query);
        GetStallUpSimpleInfoResponse response = new GetStallUpSimpleInfoResponse();
        // 本周统计
        Long nowMillions = LocalDateTimeUtils.toEpochMilli(now);
        LocalDateTime firstDayOfWeek = LocalDateTimeUtils.getFirstDayOfWeek(now);
        Long firstDayOfWeekMillions = LocalDateTimeUtils.getStartOfDay(firstDayOfWeek);
        List<StallUpDetail> weekList = endList.stream()
                .filter(v -> timeFilter.apply(v.getRealEndTime().getTime(), firstDayOfWeekMillions)
                        && timeFilter.apply(nowMillions, v.getRealEndTime().getTime()))
                .collect(Collectors.toList());
        response.setWeekDetail(StallUpBizTotal.builder().stallUpNum(weekList.size())
                .bizNum(weekList.stream().mapToInt(v -> v.getBizTotal()).sum()).build());
        // 今日待办
        Long startOfNextDay = LocalDateTimeUtils.getStartOfDay(now.plusDays(1));
        response.setTodayToDo(prepareList.stream()
                .filter(v -> timeFilter.apply(startOfNextDay - 1, v.getStartTime().getTime())).count());
        // 本周待办
        response.setWeekToDo(prepareList.stream()
                .filter(v -> timeFilter.apply(startOfNextWeek - 1, v.getStartTime().getTime())).count());
        // 本月已办
        LocalDateTime firstDayOfMonth = LocalDateTimeUtils.getFirstDayOfMonth(now);
        Long firstDayOfMonthMillions = LocalDateTimeUtils.getStartOfDay(firstDayOfMonth);
        response.setMonthDone(endList.stream()
                .filter(v -> timeFilter.apply(v.getRealEndTime().getTime(), firstDayOfMonthMillions)).count());
        // 已开始
        if (startList != null && startList.size() > 0) {
            StallUpDetail detail = startList.get(0);
            StallUpDetailVO startDetail = new StallUpDetailVO();
            startDetail.setId(detail.getId());
            startDetail.setAddress(detail.getAddress());
            startDetail.setLocation(detail.getLocation());
            response.setStartedDetail(startDetail);
            response.setWeekToDo(response.getWeekToDo() + 1);
            response.setTodayToDo(response.getTodayToDo() + 1);
        }
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<GetDetailResponse> getDetail(Long id, String mobile) {
        ApiResult<StallUpActivity> result = check(id, mobile);
        if (!result.isSuccess()) {
            log.error("[getDetail] id, mobile not match, id:{}, mobile:{}", id, mobile);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        StallUpActivity stallUpActivity = result.getData();
        // 业务办理
        List<Long> bizIdList = GsonUtils.fromGson2Obj(stallUpActivity.getBizList(), new TypeToken<List<Long>>() {
        }.getType());
        List<StallUpBizType> bizTypeList = new ArrayList<>();
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        Map<Long, StallUpBizType> bizMap = config.getBizMap();
        if (bizIdList != null && bizIdList.size() > 0) {
            bizIdList.forEach(v -> {
                StallUpBizType stallUpBizType = bizMap.get(v);
                StallUpBizType newBizType = new StallUpBizType();
                BeanUtils.copyProperties(stallUpBizType, newBizType);
                newBizType.setNum(0);
                bizTypeList.add(newBizType);
            });
            Collections.sort(bizTypeList);
        }
        // 营销功能
        StallUpMarketingNumberQuery marketingNumberQuery = new StallUpMarketingNumberQuery();
        marketingNumberQuery.setActivityId(id);
        StallUpMarketingNumber stallUpMarketingNumber = stallUpMarketingNumberMapper.get(marketingNumberQuery);
        GetDetailResponse response = new GetDetailResponse();
        if (stallUpActivity.getCustIdList() != null) {
            response.setCustIdList(GsonUtils.fromJsonToList(stallUpActivity.getCustIdList(), String[].class));
        }
        response.setBizList(bizTypeList);
        response.setMarketList(config.getMarketList().stream().map(v -> toBizTypeVO(v)).collect(Collectors.toList()));
        if (stallUpMarketingNumber != null) {
            response.setBizRemark(stallUpMarketingNumber.getRemark());
            String detail = stallUpMarketingNumber.getDetail();
            if (StringUtils.hasText(detail)) {
                List<StallUpBizDetail> list = GsonUtils.fromGson2Obj(detail, new TypeToken<List<StallUpBizDetail>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    Map<Long, Integer> map = new HashMap<>();
                    list.forEach(v -> map.put(v.getId(), v.getNum()));
                    response.setBizList(bizTypeList.stream().map(v -> {
                        v.setNum(map.get(v.getId()));
                        return v;
                    }).collect(Collectors.toList()));
                }
            }
        }
        response.setLocation(stallUpActivity.getLocation());
        response.setCommunityId(stallUpActivity.getCommunityId());
        response.setAddress(stallUpActivity.getAddress());
        response.setTitle(stallUpActivity.getTitle());
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<StallUpConfig.ConfigDetail> getConfig() {
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        StallUpConfig.ConfigDetail resConfig = new StallUpConfig.ConfigDetail();
        resConfig.setBizList(config.getBizList());
        resConfig.setMarketList(config.getMarketList());
        return ApiResult.of(0, resConfig);
    }

    @Override
    public ApiResult<Boolean> saveBiz(StallUpEndRequest request) {
        ApiResult<StallUpActivity> result = check(request.getId(), request.getMobile());
        if (!result.isSuccess()) {
            log.error("[saveBiz] id, mobile not match, request:{}", request);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        StallUpActivity stallUpActivity = result.getData();
        if (!stallUpActivity.getStatus().equals(StallUpStatusEnum.STARTED.getId())) {
            log.error("[saveBiz] status is not started, request:{}", request);
            throw new ApiException(StallUpErrorCodes.SAVE_STATUS_ERROR);
        }
        StallUpMarketingNumberQuery query = new StallUpMarketingNumberQuery();
        query.setActivityId(request.getId());
        StallUpMarketingNumber stallUpMarketingNumber = stallUpMarketingNumberMapper.get(query);
        if (stallUpMarketingNumber == null) {
            // 插入摆摊办理量
            stallUpMarketingNumberMapper
                    .insert(StallUpMarketingNumber.builder().userId(request.getUid()).activityId(request.getId())
                            .count((request.getBizDetailList() == null || request.getBizDetailList().size() == 0) ? 0
                                    : request.getBizDetailList().stream().mapToInt(v -> v.getNum()).sum())
                            .detail(toJson.apply(request.getBizDetailList())).remark(request.getBizRemark()).build());
        } else {
            // 更新摆摊办理量
            stallUpMarketingNumberMapper.update(StallUpMarketingNumber.builder().id(stallUpMarketingNumber.getId())
                    .count((request.getBizDetailList() == null || request.getBizDetailList().size() == 0) ? 0
                            : request.getBizDetailList().stream().mapToInt(v -> v.getNum()).sum())
                    .detail(toJson.apply(request.getBizDetailList())).remark(request.getBizRemark()).build());
        }
        return ApiResult.of(0, true);
    }

    @Override
    public ApiResult<List<GetGridUserTree>> getGridUserTree(String mobile, String gridId) {
        ApiResult<GridUserListResponse> result = huaWeiService
                .getGridUserList(HuaWeiRequest.builder().mobile(mobile).gridId(gridId).build());
        if (!result.isSuccess()) {
            log.error("[getGridUserTree] failed call huaWeiService.getGridUserInfo, mobile:{}, result:{}", mobile,
                    result);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        List<GridUserDetail> userList = result.getData().getGetGridUserList();
        List<GetGridUserTree> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userList)) {
            Map<String, List<GetGridUserTree.ChildrenBean>> map = new HashMap<>();
            userList.forEach(v -> {
                String[] roleArray = v.getRole().split("、");
                for (String role : roleArray) {
                    if (!StringUtils.hasText(role)) {
                        continue;
                    }
                    map.compute(role, (key, value) -> {
                        if (value == null) {
                            value = new ArrayList<>();
                        }
                        GetGridUserTree.ChildrenBean bean = new GetGridUserTree.ChildrenBean();
                        bean.setText(v.getName());
                        bean.setSeMobile(v.getSeMobile());
                        value.add(bean);
                        return value;
                    });
                }
            });
            map.forEach((k, v) -> {
                GetGridUserTree response = new GetGridUserTree();
                response.setText(k);
                response.setChildren(v);
                list.add(response);
            });
        }
        return ApiResult.of(0, list);
    }

    @Override
    public ApiResult<GetParentListResponse> getParentList(GetParentListRequest request) {
        GetParentListResponse response = new GetParentListResponse();
        Page result = PageHelper.startPage(request.getCurrentPage(), request.getPageSize(), true);
        PageHelper.orderBy("gmt_create desc");
        Long endTime = request.getEndTime();
        Long startTime = request.getStartTime();
        ParentStallUpActivityQuery query = new ParentStallUpActivityQuery();
        if (endTime != null) {
            query.setLtCreateTime(new Date(endTime));
        }
        if (startTime != null) {
            query.setGtCreateTime(new Date(startTime));
        }
        query.setGridId(request.getGridId());
        query.setStatus(request.getStatus());
        List<ParentStallUpActivity> list;
        String seMobile = request.getSeMobile();
        String mobile = StringUtils.hasText(seMobile) ? AESUtil.decrypt(seMobile, aeskey) : null;
        if (mobile == null) {
            list = parentStallUpActivityMapper.find(query);
        } else {
            query.setMobile(mobile);
            list = parentStallUpActivityMapper.getList(query);
        }
        response.setRows(list.stream().map(v -> {
            GetParentListResponse.ListBean bean = new GetParentListResponse.ListBean();
            bean.setId(v.getId());
            bean.setTitle(v.getTitle());
            bean.setCreateTime(format1.format(v.getGmtCreate()));
            bean.setRealStartTime(v.getRealStartTime() == null ? null : format1.format(v.getRealStartTime()));
            if (v.getStatus().equals(StallUpStatusEnum.END.getId())) {
                bean.setRealEndTime(v.getRealEndTime() == null ? null : format1.format(v.getRealEndTime()));
            }
            List<GridUserDetail> gridUserDetailList = GsonUtils.fromGson2Obj(v.getPartner(),
                    new TypeToken<List<GridUserDetail>>() {
                    }.getType());
            bean.setPartnerList(gridUserDetailList.stream().map(v1 -> v1.getName()).collect(Collectors.toList()));
            if (v.getStatus().equals(StallUpStatusEnum.END.getId()) && v.hasException()) {
                // 异常结束
                bean.setStatus(4);
            } else {
                bean.setStatus(v.getStatus());
            }
            return bean;
        }).collect(Collectors.toList()));
        response.setTotalCount(result.getTotal());
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<GetParentDetailResponse> getParentDetail(Long id) {
        Assert.notNull(id, "id must not be null");
        ParentStallUpActivityQuery query = new ParentStallUpActivityQuery();
        query.setId(id);
        ParentStallUpActivity parent = parentStallUpActivityMapper.get(query);
        if (parent == null) {
            log.error("[getParentDetail] parent is not exist, id:{}", id);
            throw new ApiException(StallUpErrorCodes.BASE_ERROR);
        }
        GetParentDetailResponse response = new GetParentDetailResponse();
        response.setId(parent.getId());
        response.setTitle(parent.getTitle());
        response.setAddress(parent.getAddress());
        response.setCreateName(parent.getName());
        response.setCreateTime(format1.format(parent.getGmtCreate()));
        Date realStartTime = parent.getRealStartTime();
        if (realStartTime != null) {
            response.setRealStartTime(format1.format(realStartTime));
        }
        Date realEndTime = parent.getRealEndTime();
        if (parent.getStatus().equals(StallUpStatusEnum.END.getId()) && realEndTime != null) {
            response.setRealEndTime(format1.format(realEndTime));
        }
        if (parent.getStatus().equals(StallUpStatusEnum.END.getId()) && parent.hasException()) {
            // 异常结束
            response.setStatus(4);
        } else {
            response.setStatus(parent.getStatus());
        }
        StallUpActivityQuery stallUpActivityQuery = new StallUpActivityQuery();
        stallUpActivityQuery.setBizType(BIZ_TYPE);
        stallUpActivityQuery.setParentId(id);
        // 手机号， bizList 解析bizList->StallUpBizType(List) 获得id 和数量
        List<StallUpDetail> children = stallUpActivityMapper.getList(stallUpActivityQuery);
        // key->ID， 获取名字
        Map<Long, StallUpBizType> map = stallUpConfig.getConfig().getMap();

        Map<String, String> useMobileMap = new HashMap<>();
        Map<Long, Map<String, GetParentDetailResponse.BizListBean.UserListBean>> userBeanMap = new HashMap<>();
        for (StallUpDetail detail : children) {
            useMobileMap.put(detail.getMobile(), detail.getName());
            List<StallUpBizType> bizDetails;
            if (detail.getDetail() != null) {
                bizDetails = GsonUtils.fromGson2Obj(detail.getDetail(), BIZ_DETAIL_TYPE);
            } else {
                String bizList = detail.getBizList();
                List<Long> list = GsonUtils.fromGson2Obj(bizList, new TypeToken<List<Long>>() {
                }.getType());
                bizDetails = list.stream().map(v -> {
                    StallUpBizType stallUpBizType = new StallUpBizType();
                    stallUpBizType.setId(v);
                    stallUpBizType.setNum(0);
                    return stallUpBizType;
                }).collect(Collectors.toList());
            }
            for (StallUpBizType bizDetail : bizDetails) {
                GetParentDetailResponse.BizListBean.UserListBean userBean = new GetParentDetailResponse.BizListBean.UserListBean();
                userBean.setName(detail.getName());
                userBean.setNum(bizDetail.getNum());
                userBeanMap.compute(bizDetail.getId(), (key, value) -> {
                    if (value == null) {
                        value = new TreeMap<>();
                    }
                    value.put(detail.getMobile(), userBean);
                    return value;
                });
            }
        }
        // 填充
        response.setBizList(userBeanMap.entrySet().stream().map(entry -> {
            GetParentDetailResponse.BizListBean bizListBean = new GetParentDetailResponse.BizListBean();
            bizListBean.setBizName(map.get(entry.getKey()).getName());
            // 人员填充
            Map<String, GetParentDetailResponse.BizListBean.UserListBean> userMap = entry.getValue();
            bizListBean.setTotal(
                    userMap.values().stream().mapToInt(GetParentDetailResponse.BizListBean.UserListBean::getNum).sum());
            useMobileMap.forEach((mob, name) -> {
                if (userMap.get(mob) == null) {
                    GetParentDetailResponse.BizListBean.UserListBean userBean = new GetParentDetailResponse.BizListBean.UserListBean();
                    userBean.setName(name);
                    userBean.setNum(0);
                    entry.getValue().put(mob, userBean);
                }
            });
            bizListBean.setUserList(new ArrayList<>(userMap.values()));
            return bizListBean;
        }).collect(Collectors.toList()));

        if (!CollectionUtils.isEmpty(children)) {
            response.setPartnerDetailList(children.stream().map(v -> {
                GetParentDetailResponse.PartnerDetailListBean bean = new GetParentDetailResponse.PartnerDetailListBean();
                bean.setName(v.getName());
                bean.setRealStartTime(v.getRealStartTime() == null ? null : format1.format(v.getRealStartTime()));
                bean.setRealEndTime(v.getRealEndTime() == null ? null : format1.format(v.getRealEndTime()));
                bean.setHasException(StallUpExceptionEnum.isExceptionEnd(v.getStatus()));
                if (bean.isHasException()) {
                    bean.setExceptionMsg(StallUpExceptionEnum.getByStatus(v.getStatus()).getExceptionMsg());
                }
                return bean;
            }).collect(Collectors.toList()));
        }
        return ApiResult.of(0, response);
    }

    @Override
    public ApiResult<GetParentSimpleResponse> getParentSimple(List<String> gridIds) {
        GetParentSimpleResponse response = new GetParentSimpleResponse();
        // 待开始
        ParentStallUpActivityQuery query = new ParentStallUpActivityQuery();
        query.setStatus(StallUpStatusEnum.PREPARE.getId());
        query.setGridIds(gridIds);
        // 计划时间在明天之前
        query.setLtStartTime(new Date(LocalDateTimeUtils.getStartOfDay(LocalDateTime.now().plusDays(1))));
        List<ParentStallUpActivity> prepareList = parentStallUpActivityMapper.countList(query);
        // 进行中
        query.setLtStartTime(null);
        query.setStatus(StallUpStatusEnum.STARTED.getId());
        List<ParentStallUpActivity> startList = parentStallUpActivityMapper.countList(query);
        // 已结束
        query.setStatus(StallUpStatusEnum.END.getId());
        query.setGtRealEndTime(new Date(LocalDateTimeUtils.getStartOfDay(LocalDateTime.now())));
        List<ParentStallUpActivity> endList = parentStallUpActivityMapper.countList(query);

        Map<String, Long> prepareMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(prepareList)) {
            prepareList.forEach(v -> prepareMap.put(v.getGridId(), v.getCount()));
        }
        Map<String, Long> startMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(startList)) {
            startList.forEach(v -> startMap.put(v.getGridId(), v.getCount()));
        }
        Map<String, Long> endMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(endList)) {
            endList.forEach(v -> endMap.put(v.getGridId(), v.getCount()));
        }
        response.setPrepareMap(prepareMap);
        response.setStartMap(startMap);
        response.setEndMap(endMap);
        return ApiResult.of(0, response);
    }

    @Override
    public List<Long> getGridBiz(String uid) {
        UserConfigQuery query = new UserConfigQuery();
        query.setUserId(uid);
        UserConfigDO uc = userConfigMapper.get(query);
        if (uc == null || !StringUtils.hasText(uc.getGridBiz())) {
            return GsonUtil.fromJsonToList(defaultBiz, Long[].class);
        }
        return GsonUtil.fromJsonToList(uc.getGridBiz(), Long[].class);
    }

    @Override
    public ApiResult<Void> updateHomePageGridBiz(String uid, List<Long> gridBiz) {
        UserConfigQuery query = new UserConfigQuery();
        query.setUserId(uid);
        UserConfigDO uc = userConfigMapper.get(query);
        if (uc == null) {
            UserConfigDO ins = new UserConfigDO();
            ins.setUserId(uid);
            ins.setGridBiz(GsonUtil.toJson(gridBiz));
            userConfigMapper.insert(ins);
        } else {
            UserConfigDO upd = new UserConfigDO();
            upd.setId(uc.getId());
            upd.setGridBiz(GsonUtil.toJson(gridBiz));
            userConfigMapper.update(upd);
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult<SmsHotResponse> getSmsHot(Long activityId) {
        List<GetHuaWeiSmsHotRequest> activityList = new ArrayList<>();
        GetHuaWeiSmsHotRequest request = new GetHuaWeiSmsHotRequest();
        request.setActivityId(ID_PREFIX + activityId);
        activityList.add(request);
        Map<String,Object> map = new HashMap<>();
        map.put("activityList",activityList);
        ApiResult<Map<String, Object>> dispatch = thirdApiMappingService.dispatch(map, HuaweiStallUpUrlEnum.QUERY_ACTIVITY_ORDER.getMethod());
        Map<String, Object> data = dispatch.getData();
        Object o = data.get("smsHotResultList");
        List<SmsHotResponse> smsHotResultList = GsonUtils.fromJsonToList(GsonUtils.toJson(o), SmsHotResponse[].class);
        System.out.println(JSON.toJSON(smsHotResultList));
        if (CollectionUtils.isEmpty(smsHotResultList)) {
            return ApiResult.of(0);
        }
        SmsHotResponse smsHotResponse = smsHotResultList.get(0);
        List<SmsHotResponse.SmsData> smsActivityList = smsHotResponse.getSmsActivityList();
        if (CollectionUtils.isEmpty(smsActivityList)) {
            smsHotResponse.setClientCountTotal(0L);
            smsHotResponse.setSmsActivityList(new ArrayList<>());
        }else {
            Long total = 0L;
            for (SmsHotResponse.SmsData smsData: smsActivityList) {
                if (smsData.getClientCount() != null) {
                    total +=Long.valueOf(smsData.getClientCount());
                }
            }
            smsHotResponse.setClientCountTotal(total);
        }
        return ApiResult.of(0,smsHotResultList.get(0));
    }

    /**
     * 获取待开始和已开始的VO
     */
    private StallUpDetailVO getDetailVO(StallUpDetail detail) {
        StallUpDetail tmp = new StallUpDetail();
        tmp.setId(detail.getId());
        tmp.setTitle(detail.getTitle());
        tmp.setAddress(detail.getAddress());
        tmp.setStartTime(detail.getStartTime());
        tmp.setEndTime(detail.getEndTime());
        tmp.setLocation(detail.getLocation());
        tmp.setBizList(detail.getBizList());
        tmp.setCommunityId(detail.getCommunityId());
        StallUpDetailVO stallUpDetailVO = stallUpDetail2VO(tmp);

        List<Long> bizIdList = GsonUtils.fromGson2Obj(detail.getBizList(), new TypeToken<List<Long>>() {
        }.getType());
        if (bizIdList != null && bizIdList.size() > 0) {
            bizIdList = bizIdList.stream().filter(v -> !v.equals(1L)).collect(Collectors.toList());
        }
        StallUpConfig.ConfigDetail config = stallUpConfig.getConfig();
        Map<Long, StallUpBizType> bizMap = config.getBizMap();
        if (bizIdList != null && bizIdList.size() > 0) {
            StringBuilder sb = new StringBuilder(
                    Optional.ofNullable(bizMap.get(bizIdList.get(0))).map(v -> v.getName()).orElse(""));
            for (int i = 1; i < bizIdList.size(); i++) {
                sb.append("+")
                        .append(Optional.ofNullable(bizMap.get(bizIdList.get(i))).map(v -> v.getName()).orElse(""));
            }
            stallUpDetailVO.setBizTypeStr(sb.toString());
        }
        return stallUpDetailVO;
    }

    /**
     * 获取已结束的VO
     */
    private StallUpDetailVO getEndDetail(StallUpDetail detail) {
        StallUpDetail tmp = new StallUpDetail();
        tmp.setId(detail.getId());
        tmp.setTitle(detail.getTitle());
        tmp.setBizTotal(detail.getBizTotal());
        tmp.setRealStartTime(detail.getRealStartTime());
        tmp.setRealEndTime(detail.getRealEndTime());
        tmp.setStatus(detail.getStatus());
        return stallUpDetail2VO(tmp);
    }

    /**
     * 校验摆摊id和用户手机号是否绑定
     */
    private ApiResult<StallUpActivity> check(Long id, String mobile) {
        StallUpActivityQuery query = new StallUpActivityQuery();
        query.setId(id);
        StallUpActivity stallUpActivity = stallUpActivityMapper.get(query);
        // 摆摊id和用户手机号不对应
        if (stallUpActivity == null || stallUpActivity.getMobile() == null
                || !stallUpActivity.getMobile().equals(mobile)) {
            return ApiResult.fail(-1);
        }
        return ApiResult.success(stallUpActivity);
    }

    private StallUpDetailVO stallUpDetail2VO(StallUpDetail v) {
        StallUpDetailVO stallUpDetailVO = new StallUpDetailVO();
        stallUpDetailVO.setId(v.getId());
        stallUpDetailVO.setTitle(v.getTitle());
        stallUpDetailVO.setBizTotal(v.getBizTotal());
        stallUpDetailVO.setRealStartTime(date2String.apply(v.getRealStartTime()));
        stallUpDetailVO.setRealEndTime(date2String.apply(v.getRealEndTime()));
        stallUpDetailVO.setStartTime(date2String.apply(v.getStartTime()));
        stallUpDetailVO.setEndTime(date2String.apply(v.getEndTime()));
        stallUpDetailVO.setCommunityId(v.getCommunityId());
        if (v.getStatus() != null) {
            if (v.getStatus().equals(StallUpStatusEnum.AUTO_END.getId())) {
                stallUpDetailVO.setIsAutoEnd(true);
            } else {
                stallUpDetailVO.setIsAutoEnd(false);
            }
        }
        stallUpDetailVO.setAddress(v.getAddress());
        stallUpDetailVO.setLocation(v.getLocation());
        return stallUpDetailVO;
    }

    private StallUpBizType toBizTypeVO(StallUpBizType v) {
        StallUpBizType stallUpBizType = new StallUpBizType();
        stallUpBizType.setId(v.getId());
        stallUpBizType.setName(v.getName());
        stallUpBizType.setIsDisplay(v.getIsDisplay());
        stallUpBizType.setType(v.getType());
        stallUpBizType.setIcon(v.getIcon());
        if (v.getType() == 1) {
            stallUpBizType.setUrl(v.getUrl());
        }
        return stallUpBizType;
    }

    /**
     * 更新父摆摊
     */
    private void updateParent(StallUpActivity activity, Date realStartTime, Date realEndTime) {
        Long parentId = activity.getParentId();
        // 查询父摆摊
        ParentStallUpActivityQuery query = new ParentStallUpActivityQuery();
        query.setId(parentId);
        ParentStallUpActivity parent = parentStallUpActivityMapper.get(query);
        if (parent == null) {
            return;
        }
        ParentStallUpActivity updateParent = new ParentStallUpActivity();
        updateParent.setId(parent.getId());
        updateParent.setFlag(parent.getFlag());
        Integer status = activity.getStatus();
        // 子摆摊签到
        if (status.equals(StallUpStatusEnum.STARTED.getId())) {
            if (parent.getStatus().equals(StallUpStatusEnum.PREPARE.getId())) {
                // 第一个签到
                updateParent.setRealStartTime(realStartTime);
                updateParent.setStatus(StallUpStatusEnum.STARTED.getId());
                parentStallUpActivityMapper.update(updateParent);
            }
            return;
        }
        // 子摆摊取消、结束
        boolean update = false;
        if (StallUpExceptionEnum.isExceptionEnd(status)) {
            // 异常结束
            updateParent.addExceptionFlag();
            update = true;
        }
        // 查子摆摊
        StallUpActivityQuery stallUpActivityQuery = new StallUpActivityQuery();
        stallUpActivityQuery.setParentId(parentId);
        List<StallUpActivity> children = stallUpActivityMapper.find(stallUpActivityQuery);
        List<StallUpActivity> notEndList = children.stream()
                .filter(v -> v.getStatus().equals(StallUpStatusEnum.PREPARE.getId())
                        || v.getStatus().equals(StallUpStatusEnum.STARTED.getId()))
                .collect(Collectors.toList());
        // 父摆摊是否结束
        if (CollectionUtils.isEmpty(notEndList)) {
            updateParent.setStatus(StallUpStatusEnum.END.getId());
            updateParent.setRealEndTime(realEndTime);
            update = true;
        }
        if (update) {
            parentStallUpActivityMapper.update(updateParent);
        }
    }

}
