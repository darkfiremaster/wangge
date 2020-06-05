package com.shinemo.todo.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 17:37
 * @Desc
 */
@Data
public class TodoLogDO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String company;

    private Integer thirdType;

    private String thirdId;

    private String operatorMobile;

    /**
     * 状态 1:成功,0:失败
     */
    private Integer status;

    private Long costTime;

    private String request;

    private String response;

    private String exception;

    private String remark;
}
