package com.shinemo.wangge.core.service.operate.impl;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.utils.RedisKeyUtil;
import com.shinemo.stallup.domain.utils.SubTableUtils;
import com.shinemo.wangge.core.service.operate.OperateService;
import com.shinemo.wangge.dal.mapper.UserOperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;

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


    @Override
    @Async
    public ApiResult<Void> addUserOperateLog(UserOperateLogVO userOperateLogVO) {
        Assert.notNull(userOperateLogVO.getType(), "type is null");
        Assert.notNull(userOperateLogVO.getMobile(), "mobile is null");
        insertUserOperateLog(userOperateLogVO);
        return ApiResult.of(0);
    }


    private void insertUserOperateLog(UserOperateLogVO userOperateLogVO) {
        //if (userOperateLogVO.getType() == 1 && !isGridUser(userOperateLogVO)) {
        //    //非网格用户不统计登录信息
        //    log.info("[insertUserOperateLog] user is not gridUser, request:{}",userOperateLogVO);
        //    return;
        //}


        UserOperateLogDO userOperateLogDO = getUserOperateLogDO(userOperateLogVO);
        userOperateLogMapper.insert(userOperateLogDO);
    }

    private boolean isGridUser(UserOperateLogVO userOperateLogVO) {
        String userGridInfo = redisService.get(RedisKeyUtil.getUserGridInfoPrefixKey(userOperateLogVO.getMobile()));
        if (StringUtils.isEmpty(userGridInfo)) {
            return false;
        } else {
            return true;
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
