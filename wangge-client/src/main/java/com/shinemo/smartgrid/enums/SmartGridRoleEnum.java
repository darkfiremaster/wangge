package com.shinemo.smartgrid.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @Author shangkaihui
 * @Date 2020/6/10 14:05
 * @Desc
 */
@AllArgsConstructor
@Getter
public enum SmartGridRoleEnum {
    GRID_CAPTAIN("1", "网格长"),
    GRID_MANAGER("2", "网格经理"),
    BUSINESS_HALL("3", "营业厅人员"),
    DECORATOR("4", "装维人员"),
    DIRECT_SELLER("5", "直销员"),
    AGENT_BUSINESS("98", "代理商人"),
    OPERATING_PERSONNEL("99", "运营人员");

    private String id;
    private String name;
}

