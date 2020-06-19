package com.shinemo.targetcustomer.domain.model;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 类说明: 目标客户 小区-指标 联系对象
 *
 * @author 曾鹏
 */
@Data
@Setter
@Getter
public class TargetIndexCommunityDO extends BaseDO {

    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 小区名
     */
    private String communityName;

    /**
     * 小区编码
     */
    private String communityId;


    /**
     * 小区坐标
     */
    private String location;

    /**
     * 小区详细地址
     */
    private String address;

    /**
     * 潜客上限
     */
    private String upperLimit;

    /**
     * 潜客下限
     */
    private String lowerLimit;

    /**
     * 小区对象的指标ID
     */
    private Long indexId;

    private Integer sort;

    private String mobile;
}
