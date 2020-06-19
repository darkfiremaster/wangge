package com.shinemo.targetcustomer.domain.query;

import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 类说明: 目标客户 小区-指标 联系对象
 *
 * @author 曾鹏
 */
@Data
@Setter
@Getter
public class TargetIndexCommunityQuery extends QueryBase {

    private Long id;

    private Long indexId;

    private String mobile;
}
