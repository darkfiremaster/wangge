package com.shinemo.todo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author shangkaihui
 * @Date 2020/6/4 19:11
 * @Desc
 */
@Data
public class TodoLogQuery extends QueryBase {

    private Long id;

    private String company;

    private Integer thirdType;

    private String thirdId;

    private String operatorMobile;

    /**
     * 状态 1:成功,0:失败
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    //排序规则
    private String orderBy;
}
