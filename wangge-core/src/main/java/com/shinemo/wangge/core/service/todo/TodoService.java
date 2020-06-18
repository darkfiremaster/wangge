package com.shinemo.wangge.core.service.todo;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.vo.*;

import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:25
 * @Desc
 */
public interface TodoService {

    ApiResult<Void> operateTodoThing(TreeMap<String, Object> treeMap);

    ApiResult<TreeMap> getTodoThirdRequest(TodoDTO todoDTO);

    /**
     * 获取全部类别列表
     * @return
     */
    ApiResult<TodoTypeVO> getTypeList();

    ApiResult<Void> clearTypeListCache();

    /**
     * 获取代办事项列表
     * @return
     */
    ApiResult<ListVO<TodoVO>> getTodoList(TodoQuery todoQuery);

    ApiResult<TodoIndexVO> getIndexInfo();

    ApiResult<Void> insertTodoLog(TodoLogDO todoLogDO);

    ApiResult<TodoTypeVO> getAllTodoTypeList();

    ApiResult<String> getDetailRedirectUrl(TodoQuery todoQuery);
}
