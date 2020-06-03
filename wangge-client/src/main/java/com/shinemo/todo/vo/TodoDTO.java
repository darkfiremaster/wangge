package com.shinemo.todo.vo;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/6/3 10:28
 * @Desc
 */
@Data
public class TodoDTO {

    /**
     * 第三方待办事项id
     */
    private String thirdId;

    /**
     * 第三方事项类型
     */
    private Integer thirdType;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 标题
     */
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态标签
     */
    private String label;

    /**
     * 执行人手机号
     */
    private String operatorMobile;

    /**
     * 执行时间
     */
    private String operatorTime;

    /**
     * 开始时间
     */
    private String startTime;


}
