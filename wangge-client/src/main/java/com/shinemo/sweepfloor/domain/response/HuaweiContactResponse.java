package com.shinemo.sweepfloor.domain.response;

import com.shinemo.smartgrid.domain.CommonHuaweiResponse;
import lombok.Data;

/**
 * 华为返回的家庭联系人信息
 */
@Data
public class HuaweiContactResponse extends CommonHuaweiResponse {
    /** 联系人姓名 */
    private String contactName;
    /** 联系方式 */
    private String contactMobile;
    /**
     * 联系人类型
     * 1、联系人
     * 2、家庭成员
     */
    private String type;
}
