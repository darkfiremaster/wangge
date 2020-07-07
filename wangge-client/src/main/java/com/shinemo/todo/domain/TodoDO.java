package com.shinemo.todo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shinemo.client.dal.entity.Model;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.enums.TodoStatusEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 09:50
 * @Desc
 */
@Data
public class TodoDO implements Model {
    protected static final long serialVersionUID = 4753810862868386037L;

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    /**
     * 第三方代办事项id
     */
    private String thirdId;
    /**
     * 第三方类型
     * @see ThirdTodoTypeEnum
     */
    private Integer thirdType;
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
     * @see TodoStatusEnum
     */
    private Integer status;
    /**
     * 状态标签
     */
    private String label;
    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operatorTime;
    /**
     * 执行人手机号
     */
    private String operatorMobile;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 拓展字段
     */
    private String extend;

}
