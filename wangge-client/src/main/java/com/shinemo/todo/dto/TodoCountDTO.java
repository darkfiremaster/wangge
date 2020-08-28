package com.shinemo.todo.dto;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/8/28 11:24
 * @Desc 代办工单数量统计
 */
@Data
public class TodoCountDTO {


    private String mobile;

    /**
     * 代办的数量
     */
    private Integer todoCount;
}
