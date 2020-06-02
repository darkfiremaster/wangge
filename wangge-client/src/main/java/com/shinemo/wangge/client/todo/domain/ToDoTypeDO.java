package com.shinemo.wangge.client.todo.domain;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 10:16
 * @Desc
 */
@Data
public class ToDoTypeDO {

    private Long id;

    /**
     * 类型
     * @see com.shinemo.wangge.client.todo.enums.ToDoTypeEnum
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 第三方待办事项类型
     * @see com.shinemo.wangge.client.todo.enums.ThirdTypeEnum
     */
    private Integer todoType;

}
