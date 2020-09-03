package com.shinemo.wangge.web.controller.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.dto.TodoRedirectDTO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.wangge.core.service.todo.TodoRedirectUrlService;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    public void redirectPage(TodoRedirectDTO todoRedirectDTO, HttpServletRequest request, HttpServletResponse response) {
        log.info("[redirectPage] 第三方跳转请求,第三方类型:{}, request:{}", ThirdTodoTypeEnum.getById(todoRedirectDTO.getThirdType()).getName(), todoRedirectDTO);
        todoRedirectUrlService.redirectPage(todoRedirectDTO, request, response);
    }


}
