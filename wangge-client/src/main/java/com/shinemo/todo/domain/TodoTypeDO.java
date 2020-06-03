package com.shinemo.todo.domain;

import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.enums.TodoTypeEnum;
import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 10:16
 * @Desc
 */
@Data
public class TodoTypeDO {

    private Long id;

    /**
     * 类型
     * @see TodoTypeEnum
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 第三方待办事项类型
     * @see ThirdTodoTypeEnum
     */
    private Integer todoType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

}
