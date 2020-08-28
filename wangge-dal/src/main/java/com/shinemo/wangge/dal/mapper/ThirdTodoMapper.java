package com.shinemo.wangge.dal.mapper;

import com.shinemo.client.dal.mapper.Mapper;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.dto.TodoCountDTO;
import com.shinemo.todo.dto.TodoTypeDTO;
import com.shinemo.todo.query.TodoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:46
 * @Desc
 */
public interface ThirdTodoMapper extends Mapper<TodoQuery, TodoDO> {

    void delete(@Param("id") Long id);

    Integer getTodayTodoCount(@Param("mobile") String mobile);

    Integer getAllTodoCount(@Param("mobile") String mobile);

    List<TodoTypeDTO> getAllTodoTypeList(TodoQuery todoQuery);

    List<TodoCountDTO> getYuJingTimeoutCount(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
