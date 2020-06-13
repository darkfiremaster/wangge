package com.shinemo.wangge.web.controller.todo;

import com.shinemo.client.common.ListVO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.vo.TodoIndexVO;
import com.shinemo.todo.vo.TodoTypeVO;
import com.shinemo.todo.vo.TodoVO;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author shangkaihui
 * @Date 2020/6/13 18:04
 * @Desc
 */
@RestController
@RequestMapping("/todo/thing/")
@Slf4j
public class TodoController {

    @Autowired
    private TodoService todoService;


    //@PostMapping("/getTypeList")
    //public ApiResult<TodoTypeVO> getTypeList() {
    //    return todoService.getTypeList();
    //}

    @GetMapping("/clearTypeListCache")
    public ApiResult<Void> clearTypeListCache() {
        return todoService.clearTypeListCache();
    }

    @PostMapping("/getTodoList")
    public ApiResult<ListVO<TodoVO>> getTodoList(@RequestBody TodoQuery todoQuery) {
        return todoService.getTodoList(todoQuery);
    }

    @PostMapping("/getAllTodoTypeList")
    public ApiResult<TodoTypeVO>  getAllTodoTypeList() {
        return todoService.getAllTodoTypeList();
    }

    @PostMapping("/getIndexInfo")
    public ApiResult<TodoIndexVO> getIndexInfo() {
        return todoService.getIndexInfo();
    }
}
