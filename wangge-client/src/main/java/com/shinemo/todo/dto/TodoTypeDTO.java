package com.shinemo.todo.dto;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/13 20:08
 * @Desc
 */
@Data
public class TodoTypeDTO {


    /**
     * 类型
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 第三方待办事项类型
     */
    private Integer todoType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 代办数量
     */
    private Integer todoCount;

}
