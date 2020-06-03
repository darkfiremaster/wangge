package com.shinemo.todo.vo;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 11:43
 * @Desc
 */
@Data
public class TodoRequest {

    private Long timeStamp;

    private String method;

    private String sign;

    private TodoDTO todoDTO;
}
