package com.shinemo.wangge.dal.mapper;

import com.shinemo.client.dal.mapper.Mapper;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.query.TodoQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:46
 * @Desc
 */
public interface ThirdTodoMapper extends Mapper<TodoQuery, TodoDO> {

    void delete(@Param("id") Long id);
}
