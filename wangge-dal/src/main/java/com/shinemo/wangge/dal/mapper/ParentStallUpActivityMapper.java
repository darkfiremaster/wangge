package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.stallup.domain.model.ParentStallUpActivity;
import com.shinemo.stallup.domain.query.ParentStallUpActivityQuery;

import java.util.List;

/**
 * Mapper
 *
 * @author Chenzhe Mao
 * @ClassName: ParenstallUpActivityMapper
 * @Date 2020-05-22 22:08:12
 */
public interface ParentStallUpActivityMapper extends BaseMapper<ParentStallUpActivityQuery, ParentStallUpActivity> {
	List<ParentStallUpActivity> getList(ParentStallUpActivityQuery query);
	List<ParentStallUpActivity> countList(ParentStallUpActivityQuery query);
	List<ParentStallUpActivity> findHistoryForRefreshCommunity(ParentStallUpActivityQuery query);
}