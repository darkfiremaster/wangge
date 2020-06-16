package com.shinemo.targetcustomer.domain.model;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类说明:目标网格客户指标手机 实体类
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetIndexMobileDO extends BaseDO {

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
     * 手机号
     */
    private String mobile;

    /**
     * 指标ID
     */
    private Long indexId;

    /**
     * 指标数据截止日期
     */
    private Date deadlineTime;
}
