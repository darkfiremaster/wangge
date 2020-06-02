package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.stallup.domain.model.StallUpActivity;
import com.shinemo.stallup.domain.model.StallUpDetail;
import com.shinemo.stallup.domain.query.StallUpActivityQuery;

import java.util.List;

/**
* Mapper
* @ClassName: StallUpActivityMapper
* @author Chenzhe Mao
* @Date 2020-04-01 16:20:55
*/
public interface StallUpActivityMapper extends BaseMapper<StallUpActivityQuery, StallUpActivity> {
	int delete(StallUpActivity stallUpActivity);

	List<StallUpDetail> getList(StallUpActivityQuery query);

	void insertBatch(List<StallUpActivity> list);
}