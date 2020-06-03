package com.shinemo.wangge.dal.mapper;

import com.shinemo.common.db.dao.BaseMapper;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.query.TodoQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:46
 * @Desc
 */
public interface ThirdTodoMapper extends BaseMapper<TodoQuery, TodoDO> {

    void delete(@Param("id") Long id);
}
