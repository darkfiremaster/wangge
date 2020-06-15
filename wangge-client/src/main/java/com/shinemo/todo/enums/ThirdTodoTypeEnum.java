package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThirdTodoTypeEnum {
    DAO_SAN_JIAO_ORDER(1, "倒三角工单", "亚信"),
    JI_HE_ORDER(2, "稽核工单", "思特奇"),//待确定
    TOU_SU_ORDER(3, "投诉工单", "在线公司"),//待确定
    ZHUANG_WEI_ORDER(4, "装维工单", "浩鲸"),
    YU_JING_ORDER(5, "预警工单", "华为"),
    SHANG_JI_ORDER(6, "商机工单", "华为"),
    CHANNEL_VISIT(7, "渠道走访", "年华"),//公司叫年华,系统叫督导系统,功能叫渠道走访
    BAI_TAN_PLAN(8, "摆摊计划", "讯盟"),
    SAO_LOU_PLAN(9, "扫楼计划", "讯盟"),
    SAO_CUN_PLAN(10, "扫村计划", "讯盟");
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