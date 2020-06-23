package com.shinemo.todo.error;

import com.shinemo.common.tools.exception.ErrorCode;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 13:52
 * @Desc
 */
public interface TodoErrorCodes {

    ErrorCode BASE_ERROR = new ErrorCode(500, "系统错误，请稍后尝试");
    ErrorCode SIGN_ERROR = new ErrorCode(501, "签名错误");
    ErrorCode OPERATE_TYPE_ERROR = new ErrorCode(1001, "操作类型错误");
    ErrorCode DATA_NOT_EXIST = new ErrorCode(1002, "数据不存在");
    ErrorCode DATE_PARSE_ERROR = new ErrorCode(1003, "日期参数解析异常");
    ErrorCode TODO_TYPE_ERROR = new ErrorCode(1004, "代办类型错误");

}
