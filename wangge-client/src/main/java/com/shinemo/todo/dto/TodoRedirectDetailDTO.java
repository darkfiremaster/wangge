package com.shinemo.todo.dto;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 19:55
 * @Desc
 */
@Data
public class TodoRedirectDetailDTO {

    private String mobile;

    private String thirdId;

    private Integer thirdType;

    private String token;

    //默认为0,跳转首页
    private Integer redirectPage=0;

    private Long timestamp;
}
