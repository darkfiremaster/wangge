package com.shinemo.wangge.core.service.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.dto.TodoRedirectDTO;
import com.shinemo.todo.query.TodoUrlQuery;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 11:21
 * @Desc
 */
public interface TodoRedirectUrlService {
    ApiResult<String> getDetailRedirectUrl(TodoUrlQuery todoUrlQuery);

    ApiResult<Void> redirectPage(TodoRedirectDTO todoRedirectDTO, HttpServletResponse response);
}
