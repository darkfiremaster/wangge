package com.shinemo.targetcustomer.domain.query;

import com.shinemo.client.common.QueryBase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * 类说明: 目标用户指标 查询
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetCustomersIndexQuery extends QueryBase {

    private Long id;

    private String indexCode;


}
