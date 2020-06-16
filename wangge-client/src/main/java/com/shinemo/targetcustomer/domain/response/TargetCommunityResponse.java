package com.shinemo.targetcustomer.domain.response;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 类说明: 指标对应的小区信息
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetCommunityResponse extends BaseDO {

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
}
