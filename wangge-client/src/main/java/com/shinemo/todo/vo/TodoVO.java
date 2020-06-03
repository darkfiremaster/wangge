package com.shinemo.todo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 09:50
 * @Desc
 */
@Data
public class TodoVO {
    protected static final long serialVersionUID = 4753810862868386037L;

    private Long id;
    /**
     * 第三方代办事项id
     */
    private String thirdId;
    /**
     * 第三方类型
     */
    private Integer thirdType;

    /**
     * 第三方类型名称
     */
    private String thirdTypeName;

    /**
     * 标题
     */
    private String title;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态标签
     */
    private String label;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 执行时间
     */
    private LocalDateTime operatorTime;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;





}
