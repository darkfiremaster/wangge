package com.shinemo.wangge.web.controller.todo;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.vo.TodoIndexVO;
import com.shinemo.todo.vo.TodoThirdRequest;
import com.shinemo.todo.vo.TodoTypeVO;
import com.shinemo.todo.vo.TodoVO;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:07
 * @Desc
 */
@RestController
@RequestMapping("/todo")
@Slf4j
public class TodoController {

    @Autowired
    private TodoService todoService;



    @PostMapping("/operateTodoThing")
    @SmIgnore
    public ApiResult<Void> operateTodoThing(@RequestBody TodoThirdRequest todoThirdRequest) {
        return todoService.operateTodoThing(todoThirdRequest);
    }

    @PostMapping("/getTypeList")
    @SmIgnore
    public ApiResult<TodoTypeVO> getTypeList() {
        return todoService.getTypeList();
    }

    @GetMapping("/clearTypeListCache")
    @SmIgnore
    public ApiResult<Void> clearTypeListCache() {
        return todoService.clearTypeListCache();
    }

    @PostMapping("/getTodoList")
    @SmIgnore
    public ApiResult<ListVO<TodoVO>> getTodoList(@RequestBody TodoQuery todoQuery) {
        return todoService.getTodoList(todoQuery);
    }

    @PostMapping("/getIndexInfo")
    @SmIgnore
    public ApiResult<TodoIndexVO> getIndexInfo() {
        return todoService.getIndexInfo();
    }

}
