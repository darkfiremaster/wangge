package com.shinemo.wangge.core.aop;

import cn.hutool.core.map.MapUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 16:51
 * @Desc
 */
@Slf4j
@Aspect
@Component
public class TodoLogAspect {

    private long startTimeMillis = 0; // 开始时间

    private static final int SUCCESS_CODE = 0; //成功状态码
    private static final int STATUS_SUCCESS = 1; //成功
    private static final int STATUS_ERROR = 0; //失败

    @Autowired
    private TodoService todoService;

    //请求参数
    private String request="";


    @Pointcut("@annotation(com.shinemo.wangge.core.aop.TodoLog)")
    public void point() {

    }

    /**
     * @param joinPoint
     * @Description: 方法调用前触发
     * 记录开始时间
     */
    @Before("point()")
    public void before(JoinPoint joinPoint) {
        TreeMap<String, Object> request = getRequestArgs(joinPoint);
        this.request = GsonUtils.toJson(request);
        log.info("[todo-before] request:[{}]", this.request);
        startTimeMillis = System.currentTimeMillis();
    }


    @AfterThrowing(pointcut = "point()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.info("[todo-doAfterThrowing] request:[{}], exception:[{}]",  this.request, exception.getMessage());
        TodoLogDO todoLogDO = getTodoDO(joinPoint, null, exception);
        todoService.insertTodoLog(todoLogDO);
    }

    @AfterReturning(value = "point()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        log.info("[todo-afterReturn] request:[{}], result:[{}]",  this.request, result.toString());
        TodoLogDO todoLogDO = getTodoDO(joinPoint, result, null);
        todoService.insertTodoLog(todoLogDO);
    }


    private TodoLogDO getTodoDO(JoinPoint joinPoint, Object result, Throwable exception) {
        TreeMap<String, Object> treeMap = getRequestArgs(joinPoint);
        TodoDTO todoDTO = MapUtil.get(treeMap, "postBody", TodoDTO.class);
        TodoLogDO todoLog = new TodoLogDO();
        if (todoDTO.getThirdType() == null) {
            todoLog.setCompany("");
        } else {
            todoLog.setCompany(ThirdTodoTypeEnum.getById(todoDTO.getThirdType()).getCompany());
        }
        todoLog.setThirdType(todoDTO.getThirdType());
        todoLog.setThirdId(todoDTO.getThirdId());
        todoLog.setOperatorMobile(todoDTO.getOperatorMobile());
        todoLog.setCostTime(System.currentTimeMillis() - startTimeMillis);
        todoLog.setRequest(this.request);
        setStatus(result, todoLog);

        if (result != null) {
            todoLog.setResponse(result.toString());
        }
        if (exception != null) {
            todoLog.setException(exception.getMessage());
        }
        return todoLog;
    }


    private TreeMap<String, Object> getRequestArgs(JoinPoint joinPoint) {
        TreeMap<String, Object> treeMap = (TreeMap<String, Object>) joinPoint.getArgs()[0];
        return treeMap;
    }


    private void setStatus(Object result, TodoLogDO todoLog) {
        if (result == null) {
            todoLog.setStatus(STATUS_ERROR);
            return;
        }
        int code = ((ApiResult) result).getCode();
        if (code == SUCCESS_CODE) {
            todoLog.setStatus(STATUS_SUCCESS);
        } else {
            todoLog.setStatus(STATUS_ERROR);
        }
    }
}
