package com.shinemo.todo.domain;

import com.shinemo.client.dal.entity.Model;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.enums.TodoStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
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
    private Date gmtCreate;
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
    private LocalDateTime operatorTime;
    /**
     * 执行人手机号
     */
    private String operatorMobile;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 拓展字段
     */
    private String extend;

}
