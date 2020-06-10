package com.shinemo.wangge.core.service.operate.impl;

import cn.hutool.core.collection.CollUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.operate.domain.UserGridRoleDO;
import com.shinemo.operate.domain.UserOperateLogDO;
import com.shinemo.operate.query.UserGridRoleQuery;
import com.shinemo.operate.vo.UserOperateLogVO;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.smartgrid.utils.RedisKeyUtil;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.operate.OperateService;
import com.shinemo.wangge.dal.mapper.UserGridRoleMapper;
import com.shinemo.wangge.dal.mapper.UserOperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 10:18
 * @Desc
 */
@Service
@Slf4j
public class OperateServiceImpl implements OperateService {

    @Resource
    private RedisService redisService;

    @Resource
    private UserOperateLogMapper userOperateLogMapper;

    @Resource
    private UserGridRoleMapper userGridRoleMapper;


    @Override
    @Transactional
    @Async
    public ApiResult<Void> addUserOperateLog(UserOperateLogVO userOperateLogVO) {
        Assert.isNull(userOperateLogVO.getType(), "type is null");
        Assert.isNull(SmartGridContext.getMobile(), "mobile is null");

        insertUserOperateLog(userOperateLogVO);
        //updateUserGridRoleRelation();
        return ApiResult.of(0);
    }


    private void updateUserGridRoleRelation() {
        List<GridUserRoleDetail> gridUserRoleDetailList = getGridUserRoleDetailList();
        if (CollUtil.isEmpty(gridUserRoleDetailList)) {
            return;
        }
        deleteUserGridRoleByMobile();
        insertUserGridRole(gridUserRoleDetailList);
    }

    private List<GridUserRoleDetail> getGridUserRoleDetailList() {
        String gridInfoCache = redisService.get(RedisKeyUtil.getUserGridInfoPrefixKey(SmartGridContext.getMobile()));
        return GsonUtils.fromJsonToList(gridInfoCache, GridUserRoleDetail[].class);
    }

    private void insertUserOperateLog(UserOperateLogVO userOperateLogVO) {
        UserOperateLogDO userOperateLogDO = getUserOperateLogDO(userOperateLogVO);
        userOperateLogMapper.insert(userOperateLogDO);
    }

    private void insertUserGridRole(List<GridUserRoleDetail> gridUserRoleDetailList) {
        for (GridUserRoleDetail gridUserRoleDetail : gridUserRoleDetailList) {
            if (existRole(gridUserRoleDetail)) {
                //角色不为空
                for (GridUserRoleDetail.GridRole gridRole : gridUserRoleDetail.getRoleList()) {
                    UserGridRoleDO userGridRoleDO = getUserGridRoleDO(gridUserRoleDetail, gridRole);
                    userGridRoleMapper.insert(userGridRoleDO);
                }
            } else {
                //角色为空
                UserGridRoleDO userGridRoleDO = getUserGridRoleDO(gridUserRoleDetail, null);
                userGridRoleMapper.insert(userGridRoleDO);
            }
        }
    }

    private boolean existRole(GridUserRoleDetail gridUserRoleDetail) {
        return CollUtil.isNotEmpty(gridUserRoleDetail.getRoleList());
    }

    private void deleteUserGridRoleByMobile() {
        UserGridRoleQuery userGridRoleQuery = new UserGridRoleQuery();
        userGridRoleQuery.setMobile(SmartGridContext.getMobile());
        userGridRoleMapper.delete(userGridRoleQuery);
    }

    private UserGridRoleDO getUserGridRoleDO(GridUserRoleDetail gridUserRoleDetail, GridUserRoleDetail.GridRole gridRole) {
        UserGridRoleDO userGridRoleDO = new UserGridRoleDO();
        userGridRoleDO.setMobile(SmartGridContext.getMobile());
        userGridRoleDO.setCityName(gridUserRoleDetail.getCityName());
        userGridRoleDO.setCityCode(gridUserRoleDetail.getCityCode());
        userGridRoleDO.setCountyName(gridUserRoleDetail.getCountyName());
        userGridRoleDO.setCountyCode(gridUserRoleDetail.getCountyCode());
        userGridRoleDO.setGridName(gridUserRoleDetail.getName());
        userGridRoleDO.setGridId(gridUserRoleDetail.getId());
        if (gridRole == null) {
            userGridRoleDO.setRoleId("0");
            userGridRoleDO.setRoleName("无角色");
        } else {
            userGridRoleDO.setRoleName(gridRole.getName());
            userGridRoleDO.setRoleId(gridRole.getId());
        }
        return userGridRoleDO;
    }

    private UserOperateLogDO getUserOperateLogDO(UserOperateLogVO userOperateLogVO) {
        UserOperateLogDO userOperateLogDO = new UserOperateLogDO();
        userOperateLogDO.setMobile(SmartGridContext.getMobile());
        userOperateLogDO.setUsername(SmartGridContext.getUserName());
        userOperateLogDO.setUid(SmartGridContext.getUid());
        userOperateLogDO.setOperateTime(new Date());
        userOperateLogDO.setType(userOperateLogVO.getType());
        return userOperateLogDO;
    }

}
