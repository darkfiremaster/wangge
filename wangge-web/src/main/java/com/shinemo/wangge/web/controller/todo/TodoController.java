package com.shinemo.wangge.web.controller.todo;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.vo.TodoRequest;
import com.shinemo.todo.vo.TodoTypeVO;
import com.shinemo.todo.vo.TodoVO;
import com.shinemo.wangge.core.service.todo.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:07
 * @Desc
 */
@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PostMapping("/operateTodoThing")
    @SmIgnore
    public ApiResult<Void> operateTodoThing(@RequestBody TodoRequest todoRequest) {
        //todo 记录日志

        //todo 校验签名

        return todoService.operateTodoThing(todoRequest.getTodoDTO());
    }

    @PostMapping("/getTypeList")
    @SmIgnore
    public ApiResult<TodoTypeVO> getTypeList() {
        return todoService.getTypeList();
    }

    @PostMapping("/getTodoList")
    @SmIgnore
    public ApiResult<ListVO<TodoVO>> getTodoList(TodoQuery todoQuery) {
        return todoService.getTodoList(todoQuery);
    }

}
