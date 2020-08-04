package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.groupserviceday.domain.model.GroupDO;
import com.shinemo.groupserviceday.domain.model.GroupServiceDayDO;
import com.shinemo.groupserviceday.domain.query.GroupServiceDayQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 * @ClassName: TGroupServiceDayMapper
 * @author skh
 * @Date 2020-08-03 15:30:45
 */
public interface GroupServiceDayMapper extends BaseMapper<GroupServiceDayQuery, GroupServiceDayDO> {
    /**
     * 获取最近营销的集团
     */
    List<GroupDO> getLatestMarketingGroupList(@Param("mobile") String mobile);
}