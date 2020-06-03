package com.shinemo.wangge.core.service.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.vo.TodoDTO;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:25
 * @Desc
 */
public interface TodoService {

    ApiResult<Void> operateTodoThing(TodoDTO todoDTO);


    ApiResult<Void> createTodo(TodoDTO todoDTO);

    ApiResult<Void> updateTodo(TodoDTO todoDTO);

    ApiResult<Void> deleteTodo(TodoDTO todoDTO);


}
