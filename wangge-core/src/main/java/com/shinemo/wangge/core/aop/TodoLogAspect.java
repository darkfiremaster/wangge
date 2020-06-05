package com.shinemo.wangge.core.aop;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.vo.TodoDTO;
import com.shinemo.todo.vo.TodoThirdRequest;
import com.shinemo.wangge.core.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
        TodoThirdRequest todoThirdRequest = getRequestArgs(joinPoint);
        log.info("[todo-before] todoThirdRequest:[{}]", todoThirdRequest);
        startTimeMillis = System.currentTimeMillis();
    }


    @AfterThrowing(pointcut = "point()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        TodoThirdRequest todoThirdRequest = getRequestArgs(joinPoint);

        log.info("[todo-doAfterThrowing] todoThirdRequest:[{}], exception:[{}]", todoThirdRequest, exception.getMessage());

        TodoLogDO todoLogDO = getTodoDO(joinPoint, null, exception);
        todoService.insertTodoLog(todoLogDO);
    }


    @Async
    @AfterReturning(value = "point()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Object result) {
        TodoThirdRequest todoThirdRequest = getRequestArgs(joinPoint);

        log.info("[todo-afterReturn] todoThirdRequest:[{}], result:[{}]", todoThirdRequest, result.toString());

        TodoLogDO todoLogDO = getTodoDO(joinPoint, result, null);
        todoService.insertTodoLog(todoLogDO);
    }


    private TodoLogDO getTodoDO(JoinPoint joinPoint, Object result, Throwable exception) {
        TodoThirdRequest todoThirdRequest = getRequestArgs(joinPoint);
        TodoDTO todoDTO = todoThirdRequest.getPostBody();
        TodoLogDO todoLog = new TodoLogDO();
        todoLog.setCompany(ThirdTodoTypeEnum.getById(todoDTO.getThirdType()).getCompany());
        todoLog.setThirdType(todoDTO.getThirdType());
        todoLog.setThirdId(todoDTO.getThirdId());
        todoLog.setOperatorMobile(todoDTO.getOperatorMobile());
        todoLog.setCostTime(System.currentTimeMillis() - startTimeMillis);
        todoLog.setRequest(GsonUtils.toJson(todoThirdRequest));
        setStatus(result, todoLog);

        if (result != null) {
            todoLog.setResponse(result.toString());
        }
        if (exception != null) {
            todoLog.setException(exception.getMessage());
        }
        return todoLog;
    }

    private TodoThirdRequest getRequestArgs(JoinPoint joinPoint) {
        return (TodoThirdRequest) joinPoint.getArgs()[0];
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
