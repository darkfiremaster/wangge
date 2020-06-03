package com.shinemo.wangge.core.service.todo;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.todo.vo.TodoTypeVO;
import com.shinemo.todo.vo.TodoVO;

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

    /**
     * 获取全部类别列表
     * @return
     */
    ApiResult<TodoTypeVO> getTypeList();

    /**
     * 获取代办事项列表
     * @return
     */
    ApiResult<ListVO<TodoVO>> getTodoList(TodoQuery todoQuery);

}
