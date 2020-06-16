package com.shinemo.targetcustomer.domain.model;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类说明:目标网格客户指标实体类
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetCustomersIndexDO extends BaseDO {

    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     *
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 指标
     */
    private String indexCode;

}
