package com.shinemo.wangge.dal.mapper;

import org.apache.ibatis.annotations.Param;

public interface TableOperatorMapper {
	void createTable(@Param("createSql") String createSql);
}
