package com.shinemo.todo.query;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/22 11:13
 * @Desc
 */
@Data
public class TodoUrlQuery {

    /**
     * 第三方类型
     *
     */
    private Integer thirdType;

    /**
     * 执行人手机号
     */
    private String operatorMobile;

    /**
     * 第三方代办事项id
     */
    private String thirdId;
}
