package com.shinemo.wangge.core.service.operate.impl;

import com.shinemo.client.util.GsonUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisLock;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.domain.GridInfoToken;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.RedisKeyUtil;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.wangge.core.async.UserOperatorLogManager;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import com.shinemo.wangge.core.service.operate.OperateService;
import com.shinemo.wangge.core.service.user.UserService;
import com.shinemo.wangge.dal.mapper.UserOperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:18
 * @Desc
 */
@Service
@Slf4j
public class OperateServiceImpl implements OperateService {


    @Resource
    private UserOperateLogMapper userOperateLogMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLock redisLock;

    @Resource
    private UserService userService;

    @Resource
    private UserOperatorLogManager userOperatorLogManager;

    @Resource
    private SmartGridInfoService smartGridInfoService;

    private static final Integer LOGIN_EXPIRE_TIME = 15 * 60;
    public static final Integer LOCK_EXPIRE_TIME = 1;

    private static final String USER_OPERATE_LOG_LIST = "user_operate_log_list";
    private static final String SYNC_USER_OPERATE_LOG_LIST = "sync_user_operate_log_list";

    //@NacosValue(value = "${wangge.operatelog.sync.count}", autoRefreshed = true)
    //private Integer LOG_COUNT_SISE = 10;

    @Override
    public ApiResult<Void> addUserOperateLog(UserOperateLogVO userOperateLogVO) {
        insertUserOperateLog(userOperateLogVO);
        return ApiResult.of(0);
    }


    //@Override
    //public ApiResult<Void> addUserOperateLogToRedis(UserOperateLogVO userOperateLogVO) {
    //    //先插入redis,达到指定数量再插入数据库
    //    UserOperateLogDO userOperateLogDO = getUserOperateLogDO(userOperateLogVO);
    //    redisService.rpush(USER_OPERATE_LOG_LIST, GsonUtils.toJson(userOperateLogDO));
    //
    //    Long size = redisService.llen(USER_OPERATE_LOG_LIST);
    //    if (LOG_COUNT_SISE <= size.intValue()) {
    //        syncLogFromRedisToDB();
    //    }
    //
    //    return ApiResult.of(0);
    //}

    //@Override
    //public ApiResult<Void> addUserOperateLogToDB(UserOperateLogVO userOperateLogVO) {
    //    UserOperateLogDO userOperateLogDO = getUserOperateLogDO(userOperateLogVO);
    //    userOperateLogMapper.insert(userOperateLogDO);
    //    return ApiResult.of(0);
    //}


    //@Override
    //public ApiResult<Void> syncLogFromRedisToDB() {
    //    boolean hasLock = redisLock.lock(LockContext.create(SYNC_USER_OPERATE_LOG_LIST, LOCK_EXPIRE_TIME));
    //    if (hasLock) {
    //        try {
    //            Long begin = System.currentTimeMillis();
    //            List<String> logList = redisService.lrange(USER_OPERATE_LOG_LIST, 0, LOG_COUNT_SISE - 1);
    //            log.info("[syncLogFromRedisToDB] start syncLogFromRedisToDB,需要同步的数量:{}", logList.size());
    //            if (CollectionUtils.isEmpty(logList)) {
    //                return ApiResult.of(0);
    //            }
    //            List<UserOperateLogDO> logDOList = logList.stream().map(log -> GsonUtils.fromGson2Obj(log, UserOperateLogDO.class)).collect(Collectors.toList());
    //            Long count = userOperateLogMapper.insertBatch(logDOList, SubTableUtils.getTableIndexByOnlyMonth(LocalDate.now()));
    //            if (Objects.equals(count.intValue(), logDOList.size())) {
    //                //数据库新增成功,清空redis数据
    //                log.info("[syncLogFromRedisToDB] 同步数据成功,耗时:{}ms", System.currentTimeMillis() - begin);
    //                redisService.ltrim(USER_OPERATE_LOG_LIST, count, -1);
    //            }
    //        } catch (Exception e) {
    //            log.error("[syncLogFromRedisToDB] 同步数据异常,异常原因:{}", e.getMessage());
    //        } finally {
    //            redisLock.unlock(LockContext.create(SYNC_USER_OPERATE_LOG_LIST));
    //        }
    //    }
    //
    //    return ApiResult.of(0);
    //}

    @Override
    public ApiResult<String> genGridInfoToken(GridUserRoleDetail request) {
        String mobile = SmartGridContext.getMobile();
        GridInfoToken gridInfoToken = new GridInfoToken();
        if (request != null) {
            gridInfoToken.setGridDetail(request);
        } else {
            List<GridUserRoleDetail> gridUserRole = smartGridInfoService.getGridUserRole(mobile);
            if (CollectionUtils.isEmpty(gridUserRole)) {
                gridUserRole = new ArrayList<>();
                GridUserRoleDetail gridUserRoleDetail = new GridUserRoleDetail();
                gridUserRoleDetail.setId("0");
                gridUserRoleDetail.setName("无网格");
                gridUserRole.add(gridUserRoleDetail);
            }
            gridInfoToken.setGridList(gridUserRole);
        }
        String stringToken = Base64.encodeBase64URLSafeString(GsonUtils.toJson(gridInfoToken).getBytes(StandardCharsets.UTF_8));

        return ApiResult.of(0, stringToken);
    }

//    @Override
//    public ApiResult<GridInfoToken> validateGridInfoToken(String token,Integer type) {
//        Assert.notNull(token,"token is null");
//        String mobile = SmartGridContext.getMobile();
//        String usableToken = new String(Base64.decodeBase64(token), StandardCharsets.UTF_8);
//        GridInfoToken gridInfoToken = GsonUtil.fromGson2Obj(usableToken, GridInfoToken.class);
//        String requestToken = gridInfoToken.getToken();
//        String checkToken = null;
//        if (type == 1) {
//            checkToken = Md5Util.getMD5Str(mobile + SmartGridConstant.TOKEN_KEY);
//        }else if (type == 2) {
//            GridUserRoleDetail detail = gridInfoToken.getGridDetail();
//            checkToken = Md5Util.getMD5Str(mobile + SmartGridConstant.TOKEN_KEY + detail.getId());
//        }
//        if (!requestToken.equals(checkToken)) {
//            throw new ApiException(StallUpErrorCodes.GRID_TOKEN_ERROR);
//        }
//
//        return ApiResult.of(0,gridInfoToken);
//    }


    private void  insertUserOperateLog(UserOperateLogVO userOperateLogVO) {
        if (userOperateLogVO.getType() == 1) {
            //登录类型
            if (!isGridUser(userOperateLogVO)) {
                //非网格用户不统计登录信息
                log.info("[insertUserOperateLog] 非网格用户不统计登录信息, mobile:{}", userOperateLogVO.getMobile());
                return;
            }
            //15分钟内只算一次登录
            String loginFlag = redisService.get(RedisKeyUtil.getUserLoginFlagPrefixKey(userOperateLogVO.getMobile()));
            if (!StringUtils.isEmpty(loginFlag)) {
                log.info("[insertUserOperateLog] 用户15分钟内已经登录过, mobile:{}", userOperateLogVO.getMobile());
                return;
            }

            //登录成功
            redisService.set(RedisKeyUtil.getUserLoginFlagPrefixKey(userOperateLogVO.getMobile()), "true", LOGIN_EXPIRE_TIME);

            //更新用户网格信息
            String gridInfo = userOperateLogVO.getGridInfo();
            List<GridUserRoleDetail> gridUserRoleDetailList = GsonUtils.fromJsonToList(gridInfo, GridUserRoleDetail[].class);
            if (!CollectionUtils.isEmpty(gridUserRoleDetailList) && !Objects.equals(gridUserRoleDetailList.get(0).getId(), "0")) {
                //存在网格信息
                log.info("[insertUserOperateLog]  start update gridinfo,mobile:{}", userOperateLogVO.getMobile());
                userService.updateUserGridRoleRelation(gridUserRoleDetailList, userOperateLogVO.getMobile());
            }
        }

        UserOperateLogDO userOperateLogDO = getUserOperateLogDO(userOperateLogVO);
        userOperatorLogManager.addLog(userOperateLogDO);
    }


    private boolean isGridUser(UserOperateLogVO userOperateLogVO) {
        String gridInfo = userOperateLogVO.getGridInfo();
        List<GridUserRoleDetail> gridList = GsonUtil.fromJsonToList(gridInfo, GridUserRoleDetail[].class);
        if (!CollectionUtils.isEmpty(gridList) && !Objects.equals(gridList.get(0).getId(), "0")) {
            return true;
        } else {
            return false;
        }
    }


    private UserOperateLogDO getUserOperateLogDO(UserOperateLogVO userOperateLogVO) {
        UserOperateLogDO userOperateLogDO = new UserOperateLogDO();
        userOperateLogDO.setTableIndex(SubTableUtils.getTableIndexByOnlyMonth(LocalDate.now()));
        userOperateLogDO.setMobile(userOperateLogVO.getMobile());
        userOperateLogDO.setUsername(userOperateLogVO.getUserName());
        userOperateLogDO.setUid(userOperateLogVO.getUid());
        userOperateLogDO.setOperateTime(new Date());
        userOperateLogDO.setType(userOperateLogVO.getType());
        return userOperateLogDO;
    }

}
