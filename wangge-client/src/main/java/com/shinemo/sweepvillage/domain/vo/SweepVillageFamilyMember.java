package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

/**
 * 家庭成员
 */
@Data
public class SweepVillageFamilyMember {
    /** 联系人姓名 */
    private String name;
    /** 联系人手机号 */
    private String mobile;
    /** 加密手机号 */
    private String encryContactMobile;
    /** 类型 */
    private Integer type;
}
