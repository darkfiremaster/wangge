package com.shinemo.todo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 17:37
 * @Desc
 */
@Data
public class TodoLogDO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
