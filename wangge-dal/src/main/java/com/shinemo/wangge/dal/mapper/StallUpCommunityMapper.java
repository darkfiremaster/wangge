package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.operate.domain.LoginInfoResultDO;
import com.shinemo.smartgrid.domain.model.BackdoorLoginDO;
import com.shinemo.smartgrid.domain.query.BackdoorLoginQuery;
import com.shinemo.stallup.domain.model.StallUpCommunityDO;
import com.shinemo.stallup.domain.query.StallUpCommunityQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zz
 * @date 2020年6月29日 上午11:43:59
 */
public interface StallUpCommunityMapper extends BaseMapper<StallUpCommunityQuery , StallUpCommunityDO> {

    Long batchInsert(@Param("stallUpCommunityDOList") List<StallUpCommunityDO> stallUpCommunityDOList);

    List<StallUpCommunityDO> findRecentCommunity(StallUpCommunityQuery query);

}