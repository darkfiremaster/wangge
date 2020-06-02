package com.shinemo.thirdapi.common.error;

import com.shinemo.common.tools.exception.ErrorCode;

/**
 * 错误
 *
 * @author Chenzhe Mao
 * @date 2020-04-25
 */
public interface ThirdApiErrorCodes {
    ErrorCode BASE_ERROR = new ErrorCode(500, "系统繁忙，请稍后尝试！");
    ErrorCode API_TYPE_ERROR = new ErrorCode(501, "不支持该业务类型API");
    ErrorCode HUA_WEI_ERROR = new ErrorCode(502, "华为接口报错");
    ErrorCode HUA_WEI_RESPONSE_IS_NULL = new ErrorCode(503, "华为返回参数为空");
    ErrorCode HUA_WEI_RESPONSE_ERROR = new ErrorCode(504, "json数据格式错误");

    ErrorCode URL_NOT_EXIST = new ErrorCode(1000, "url不存在");
}