package com.shinemo.wangge.web.controller.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.dto.TodoRedirectDTO;
import com.shinemo.wangge.core.service.todo.TodoRedirectUrlService;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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

    @Resource
    private TodoRedirectUrlService todoRedirectUrlService;

    @PostMapping("/operateTodoThing")
    public ApiResult<Void> operateTodoThing(@RequestBody TreeMap<String, Object> treeMap) {
        return todoService.operateTodoThing(treeMap);
    }

    @GetMapping("/redirectPage")
    public void redirectPage(TodoRedirectDTO todoRedirectDTO, HttpServletResponse response) {
        todoRedirectUrlService.redirectPage(todoRedirectDTO,response);
    }

}
