package com.shinemo.targetcustomer.domain.response;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 类说明:
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetCustomerResponse  extends BaseDO {

    private List<TargetIndexResponse> indexList;

    /**
     * 手机号
     */
    private String mobile;


}
