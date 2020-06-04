package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThirdTodoTypeEnum {
    DAO_SAN_JIAO_ORDER(1, "倒三角工单", "华为"),
    JI_HE_ORDER(2, "稽核工单", "华为"),
    TOU_SU_ORDER(3, "投诉工单", "华为"),
    ZHUANG_WEI_ORDER(4, "装维工单", "华为"),
    YU_JING_ORDER(5, "预警工单", "华为"),
    SHANG_JI_ORDER(6, "商机工单", "华为"),
    CHANNEL_VISIT(7, "渠道走访", "督导"),
    BAI_TAN_PLAN(8, "摆摊计划", "讯盟"),
    SAO_LOU_PLAN(9, "扫楼计划", "讯盟"),
    SAO_CUN_PLAN(10, "扫村计划", "讯盟");
    //todo 找产品确认工单所属公司
    private int id;
    private String name;
    private String company;


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