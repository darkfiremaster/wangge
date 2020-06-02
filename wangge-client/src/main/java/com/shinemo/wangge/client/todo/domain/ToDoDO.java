package com.shinemo.wangge.client.todo.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 09:50
 * @Desc
 */
@Data
public class ToDoDO  {
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
     * @see com.shinemo.wangge.client.todo.enums.ThirdTypeEnum
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
     * @see com.shinemo.wangge.client.todo.enums.ToDoStatusEnum
     */
    private Integer status;
    /**
     * 状态标签
     */
    private String label;
    /**
     * 执行时间
     */
    private Date operatorTime;
    /**
     * 执行人手机号
     */
    private String operatorMobile;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人手机号
     */
    private String creatorMobile;
    /**
     * 拓展字段
     */
    private String extend;

}
