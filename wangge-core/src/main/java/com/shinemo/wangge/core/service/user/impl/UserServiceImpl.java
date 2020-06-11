package com.shinemo.wangge.core.service.user.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.gridinfo.domain.model.SmartGridInfoDO;
import com.shinemo.operate.domain.UserGridRoleDO;
import com.shinemo.operate.query.UserGridRoleQuery;
import com.shinemo.stallup.domain.model.GridUserRoleDetail;
import com.shinemo.wangge.core.service.gridinfo.SmartGridInfoService;
import com.shinemo.wangge.core.service.user.UserService;
import com.shinemo.wangge.dal.mapper.UserGridRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/10 19:18
 * @Desc
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserGridRoleMapper userGridRoleMapper;

    @Resource
    private SmartGridInfoService smartGridInfoService;

    @Override
    @Transactional
    public ApiResult<Void> updateUserGridRoleRelation(List<GridUserRoleDetail> gridUserRoleDetailList, String mobile) {
        UserGridRoleQuery userGridRoleQuery = new UserGridRoleQuery();
        userGridRoleQuery.setMobile(mobile);
        userGridRoleMapper.deleteByMobile(userGridRoleQuery);

        for (GridUserRoleDetail gridUserRoleDetail : gridUserRoleDetailList) {
            if (existRole(gridUserRoleDetail)) {
                //角色不为空
                for (GridUserRoleDetail.GridRole gridRole : gridUserRoleDetail.getRoleList()) {
                    UserGridRoleDO userGridRoleDO = getUserGridRoleDO(gridUserRoleDetail, gridRole,mobile);
                    userGridRoleMapper.insert(userGridRoleDO);
                }
            } else {
                //角色为空
                UserGridRoleDO userGridRoleDO = getUserGridRoleDO(gridUserRoleDetail, null,mobile);
                userGridRoleMapper.insert(userGridRoleDO);
            }
        }

        return ApiResult.of(0);
    }

    private boolean existRole(GridUserRoleDetail gridUserRoleDetail) {
        return CollUtil.isNotEmpty(gridUserRoleDetail.getRoleList());
    }

    private UserGridRoleDO getUserGridRoleDO(GridUserRoleDetail gridUserRoleDetail, GridUserRoleDetail.GridRole gridRole,String mobile) {
        UserGridRoleDO userGridRoleDO = new UserGridRoleDO();
        userGridRoleDO.setMobile(mobile);
        //如果华为数据为空,则调接口自己设值,否则的话直接用华为的数据
        if (StringUtils.isBlank(gridUserRoleDetail.getCityCode())) {
            ApiResult<SmartGridInfoDO> smartGridInfoDOResult = smartGridInfoService.getByGridId(gridUserRoleDetail.getId());
            if (smartGridInfoDOResult.isSuccess()) {
                SmartGridInfoDO smartGridInfoDO = smartGridInfoDOResult.getData();
                if (smartGridInfoDO != null) {
                    userGridRoleDO.setCityCode(smartGridInfoDO.getCityCode());
                    userGridRoleDO.setCityName(smartGridInfoDO.getCityName());
                    userGridRoleDO.setCountyCode(smartGridInfoDO.getCountyCode());
                    userGridRoleDO.setCountyName(smartGridInfoDO.getCountyName());
                }
            }
        } else {
            userGridRoleDO.setCityCode(gridUserRoleDetail.getCityCode());
            userGridRoleDO.setCityName(gridUserRoleDetail.getCityName());
            userGridRoleDO.setCountyCode(gridUserRoleDetail.getCountyCode());
            userGridRoleDO.setCountyName(gridUserRoleDetail.getCountyName());
        }

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

    private List<GridUserRoleDetail> getMockGridUserRoleDetailList(String mobile) {
        GridUserRoleDetail gridUserRoleDetail = new GridUserRoleDetail();
        gridUserRoleDetail.setId("1");
        gridUserRoleDetail.setName("网格A");
        gridUserRoleDetail.setCityName("杭州");
        gridUserRoleDetail.setCityCode("hangzhou");
        gridUserRoleDetail.setCountyName("西湖取");
        gridUserRoleDetail.setCountyCode("xihu");
        GridUserRoleDetail.GridRole gridRole = new GridUserRoleDetail.GridRole();
        gridRole.setId("1");
        gridRole.setName("网格长");
        gridUserRoleDetail.setRoleList(Lists.newArrayList(gridRole));
        return Lists.newArrayList(gridUserRoleDetail);
    }
}
