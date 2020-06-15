package com.shinemo.todo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shinemo.client.common.QueryBase;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 09:50
 * @Desc
 */
@Data
public class TodoQuery  extends QueryBase {

    private Long id;

    private String thirdId;

    /**
     * 代办类型
     * @see com.shinemo.todo.enums.ThirdTodoTypeEnum
     */
    private Integer thirdType;

    private String mobile;

    private Integer status;

    /**
     * 1:今日代办 2:全部代办
     */
    private Integer timeType;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
