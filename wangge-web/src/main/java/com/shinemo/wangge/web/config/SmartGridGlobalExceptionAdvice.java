package com.shinemo.wangge.web.config;

import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.exception.CommonErrorCodes;
import com.shinemo.common.tools.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public final class SmartGridGlobalExceptionAdvice {


    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(e.getMessage(),CommonErrorCodes.PARAM_ERROR.code);
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
