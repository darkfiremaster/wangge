package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.stallup.domain.model.StallUpMarketingNumber;
import com.shinemo.stallup.domain.query.StallUpMarketingNumberQuery;

/**
* Mapper
* @ClassName: StallUpMarketingNumberMapper
* @author Chenzhe Mao
* @Date 2020-04-01 16:20:07
*/
public interface StallUpMarketingNumberMapper extends BaseMapper<StallUpMarketingNumberQuery, StallUpMarketingNumber> {
	int delete(StallUpMarketingNumber stallUpMarketingNumber);
}