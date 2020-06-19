package com.shinemo.wangge.web.controller.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:07
 * @Desc
 */
@RestController
@RequestMapping("/todo")
@Slf4j
public class OperateTodoController {

    @Resource
    private TodoService todoService;

    @PostMapping("/operateTodoThing")
    public ApiResult<Void> operateTodoThing(@RequestBody TreeMap<String, Object> treeMap) {
        return todoService.operateTodoThing(treeMap);
    }

}
