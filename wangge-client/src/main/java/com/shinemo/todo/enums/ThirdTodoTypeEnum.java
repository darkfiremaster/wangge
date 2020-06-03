package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThirdTodoTypeEnum {
    DAO_SAN_JIAO_ORDER(1, "倒三角工单"),
    JI_HE_ORDER(2, "稽核工单"),
    TOU_SU_ORDER(3, "投诉工单"),
    ZHUANG_WEI_ORDER(4, "装维工单"),
    YU_JING_ORDER(5, "预警工单"),
    SHANG_JI_ORDER(6, "商机工单"),
    CHANNEL_VISIT(7, "渠道走访"),
    BAI_TAN_PLAN(8, "摆摊计划"),
    SAO_LOU_PLAN(9, "扫楼计划"),
    SAO_CUN_PLAN(10, "扫村计划");

    private int id;
    private String name;

    public static ThirdTodoTypeEnum getById(int id) {
        ThirdTodoTypeEnum[] e = ThirdTodoTypeEnum.values();
        for (ThirdTodoTypeEnum iter : e) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }


}