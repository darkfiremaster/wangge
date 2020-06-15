package com.shinemo.todo.vo;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 17:20
 * @Desc
 */
@Data
public class TodoIndexVO {
    /**
     * 今日代办数量
     */
    private Integer todayTodoCount;

    /**
     * 全部代办数量
     */
    private Integer allTodoCount;
}
