package com.shinemo.wangge.core.service.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.query.TodoLogQuery;
import com.shinemo.todo.query.TodoQuery;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:25
 * @Desc
 */
public interface TodoLogService {


    ApiResult<List<TodoLogDO>> getTodoLogList(TodoLogQuery todoLogQuery);

    ApiResult<List<TodoDO>> getTodoList(TodoQuery todoQuery);
}
