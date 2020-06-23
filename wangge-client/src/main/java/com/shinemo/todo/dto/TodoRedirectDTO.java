package com.shinemo.todo.dto;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 19:27
 * @Desc
 */
@Data
public class TodoRedirectDTO {
    private String paramData;
    private Long timestamp;
    private String sign;
    private Integer thirdType;
}
