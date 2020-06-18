package com.shinemo.wangge.core.service.todo;

import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.todo.vo.TodoThirdRequest;

import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 16:22
 * @Desc
 */
public interface TodoAuthCheckService {

     ApiResult<TodoThirdRequest>  checkSign(TreeMap<String,Object> treeMap);
}
