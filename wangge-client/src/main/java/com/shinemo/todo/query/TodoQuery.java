package com.shinemo.todo.query;

import com.shinemo.client.common.QueryBase;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 09:50
 * @Desc
 */
@Data
public class TodoQuery  extends QueryBase {

    private Long id;

    private String thirdId;

    private Integer thirdType;

    private String mobile;

    private Integer timeType;

    private Integer status;

    private Integer todoType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
