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

    private String thirdid;

    private Integer thirdtype;

    private String token;

    //默认为0,跳转首页
    private Integer redirectpage=0;

    private Long timestamp;

    private String title;

    private String address;

    private String communityname;
    private String communityid;
    private String communityaddress;
    private String communitylocation;
}
