package com.shinemo.wangge.core.service.todo.impl;

import com.github.pagehelper.PageHelper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.query.TodoLogQuery;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.wangge.core.service.todo.TodoLogService;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import com.shinemo.wangge.dal.mapper.TodoLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/17 13:51
 * @Desc
 */
@Service
@Slf4j
public class TodoLogServiceImpl implements TodoLogService {

    @Resource
    private TodoLogMapper todoLogMapper;

    @Resource
    private ThirdTodoMapper thirdTodoMapper;


    @Override
    public ApiResult<List<TodoLogDO>> getTodoLogList(TodoLogQuery todoLogQuery) {
        PageHelper.startPage(todoLogQuery.getCurrentPage().intValue(), todoLogQuery.getPageSize().intValue());
        PageHelper.orderBy(todoLogQuery.getOrderBy());

        List<TodoLogDO> todoLogDOS = todoLogMapper.find(todoLogQuery);

        return ApiResult.of(0, todoLogDOS);
    }

    @Override
    public ApiResult<List<TodoDO>> getTodoList(TodoQuery todoQuery) {
        PageHelper.startPage(todoQuery.getCurrentPage().intValue(), todoQuery.getPageSize().intValue());
        PageHelper.orderBy("id desc");
        List<TodoDO> todoDOList = thirdTodoMapper.find(todoQuery);
        return ApiResult.of(0, todoDOList);
    }
}
