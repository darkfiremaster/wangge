package com.shinemo.wangge.core.service.todo;

import com.shinemo.todo.vo.TodoThirdRequest;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 16:22
 * @Desc
 */
public interface TodoAuthCheckService {

     Boolean checkSign(TodoThirdRequest todoThirdRequest);
}
