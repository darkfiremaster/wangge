package com.shinemo.todo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date operatorTime;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;





}
