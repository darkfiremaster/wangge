package com.shinemo.targetcustomer.domain.query;

import com.shinemo.client.common.QueryBase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 类说明:目标网格客户指标手机 实体类
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetIndexMobileQuery extends QueryBase {

    private Long id;

    /**
     * 手机号
     */
    private String mobile;

}
