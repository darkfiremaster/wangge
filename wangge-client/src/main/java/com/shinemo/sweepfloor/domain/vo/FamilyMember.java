package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

/**
 * 家庭成员
 */
@Data
public class FamilyMember {
    /** 联系人姓名 */
    private String contactName;
    /** 联系人手机号 */
    private String contactMobile;
    /** 加密手机号 */
    private String encryContactMobile;
    /** 类型 */
    private Integer type;
}
