package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.smartgrid.domain.model.BackdoorLoginDO;
import com.shinemo.smartgrid.domain.query.BackdoorLoginQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @author htdong
 * @date 2020年6月15日 上午11:43:59
 */
public interface BackdoorLoginMapper extends BaseMapper<BackdoorLoginQuery, BackdoorLoginDO> {

    void deleteByMobile(@Param("loginMobile") String loginMobile);
}