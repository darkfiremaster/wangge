package com.shinemo.targetcustomer.domain.request;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 类说明:请求
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetCustomerRequest extends BaseDO {


    /**
     * 手机号
     */
    private String mobile;
}
