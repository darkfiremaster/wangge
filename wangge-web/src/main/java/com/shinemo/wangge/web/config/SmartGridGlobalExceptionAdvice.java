package com.shinemo.wangge.web.config;

import cn.hutool.core.date.DateException;
import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.CommonErrorCodes;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.error.TodoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public final class SmartGridGlobalExceptionAdvice {


    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(CommonErrorCodes.PARAM_ERROR.msg + ":" + e.getMessage(), CommonErrorCodes.PARAM_ERROR.code);
    }

    @ExceptionHandler(DateException.class)
    public ApiResult handleDateException(DateException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(CommonErrorCodes.PARAM_ERROR.msg + ":" + e.getMessage(), CommonErrorCodes.PARAM_ERROR.code);
    }

    @ExceptionHandler(TodoException.class)
    public ApiResult handleTodoException(TodoException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(e.getMessage(), e.code);
    }

    /**
     * 处理全局异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResultWrapper.fail(CommonErrorCodes.SERVER_ERROR);
    }


}
