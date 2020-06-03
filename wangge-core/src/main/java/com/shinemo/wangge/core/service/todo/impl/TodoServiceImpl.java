package com.shinemo.wangge.core.service.todo.impl;

import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.enums.TodoMethodOperateEnum;
import com.shinemo.todo.error.TodoErrorCodes;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.wangge.core.service.todo.TodoService;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:25
 * @Desc
 */
@Service
@Slf4j
public class TodoServiceImpl implements TodoService {

    @Autowired
    private ThirdTodoMapper thirdTodoMapper;

    @Override
    public ApiResult<Void> operateTodoThing(TodoDTO todoDTO) {
        Assert.notNull(todoDTO, "param is null");
        Assert.notNull(todoDTO.getOperateType(), "operateType is null");

        if (Objects.equals(todoDTO.getOperateType(), TodoMethodOperateEnum.CREATE.getId())) {
            return createTodo(todoDTO);
        } else if (Objects.equals(todoDTO.getOperateType(), TodoMethodOperateEnum.UPDATE.getId())) {
            return updateTodo(todoDTO);
        } else if (Objects.equals(todoDTO.getOperateType(), TodoMethodOperateEnum.DELETE.getId())) {
            return deleteTodo(todoDTO);
        } else {
            return ApiResultWrapper.fail(TodoErrorCodes.OPERATE_TYPE_ERROR);
        }
    }

    @Override
    public ApiResult<Void> createTodo(TodoDTO todoDTO) {
        //校验参数
        Assert.notNull(todoDTO.getThirdId(), "thirdId is null");
        Assert.notNull(todoDTO.getThirdType(), "thirdType is null");
        Assert.notNull(todoDTO.getOperateType(), "operateType is null");
        Assert.notNull(todoDTO.getOperateType(), "operatorMobile is null");

        Assert.notNull(todoDTO.getTitle(), "title is null");
        Assert.notNull(todoDTO.getRemark(), "remark is null");
        Assert.notNull(todoDTO.getStatus(), "status is null");
        Assert.notNull(todoDTO.getLabel(), "label is null");
        Assert.notNull(todoDTO.getOperatorTime(), "operatorTime is null");

        //转换对象
        TodoDO todoDO = getTodoDO(todoDTO);
        //新增
        thirdTodoMapper.insert(todoDO);

        log.info("[createTodo] create todo success");
        return ApiResult.of(0);
    }


    @Override
    public ApiResult<Void> updateTodo(TodoDTO todoDTO) {
        //校验参数
        Assert.notNull(todoDTO.getThirdId(), "thirdId is null");
        Assert.notNull(todoDTO.getThirdType(), "thirdType is null");
        Assert.notNull(todoDTO.getOperateType(), "operateType is null");
        Assert.notNull(todoDTO.getOperateType(), "operatorMobile is null");

        //转换对象
        TodoDO todoDO = getTodoDO(todoDTO);
        //修改
        thirdTodoMapper.update(todoDO);

        log.info("[updateTodo] update todo success");
        return ApiResult.of(0);
    }

    @Override
    public ApiResult<Void> deleteTodo(TodoDTO todoDTO) {
        //校验参数
        Assert.notNull(todoDTO.getThirdId(), "thirdId is null");
        Assert.notNull(todoDTO.getThirdType(), "thirdType is null");
        Assert.notNull(todoDTO.getOperateType(), "operateType is null");
        Assert.notNull(todoDTO.getOperateType(), "operatorMobile is null");

        //先查再删
        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setThirdId(todoDTO.getThirdId());
        todoQuery.setThirdType(todoDTO.getThirdType());
        todoQuery.setMobile(todoDTO.getOperatorMobile());

        TodoDO todoDO = thirdTodoMapper.get(todoQuery);
        if (todoDO == null) {
            return ApiResultWrapper.fail(TodoErrorCodes.DATA_NOT_EXIST);
        }

        //删除
        thirdTodoMapper.delete(todoDO.getId());

        log.info("[deleteTodo] delete todo success");
        return ApiResult.of(0);
    }

    private TodoDO getTodoDO(TodoDTO todoDTO) {
        TodoDO todoDO = new TodoDO();
        BeanUtils.copyProperties(todoDTO, todoDO);
        return todoDO;
    }
}
