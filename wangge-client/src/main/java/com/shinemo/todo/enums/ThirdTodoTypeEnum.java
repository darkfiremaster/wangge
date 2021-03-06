package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代办事项类型枚举
 */
@Getter
@AllArgsConstructor
public enum ThirdTodoTypeEnum {
    DAO_SAN_JIAO_ORDER(1, "倒三角工单", "亚信"), //已对接
    JI_HE_ORDER(2, "稽核工单", "思特奇"),//待确定
    TOU_SU_ORDER(3, "投诉工单", "在线公司"),//待确定
    ZHUANG_YI_ORDER(4, "装移工单", "浩鲸"), //已对接
    YU_JING_ORDER(5, "预警工单", "华为"), //已对接
    SHANG_JI_ORDER(6, "商机工单", "华为"), //待确定
    CHANNEL_VISIT(7, "渠道走访", "年华"),//已对接,公司叫年华,系统叫督导系统,功能叫渠道走访
    BAI_TAN_PLAN(8, "摆摊", "讯盟"),//已对接
    SAO_LOU_PLAN(9, "扫楼", "讯盟"),//待确定
    SAO_CUN_PLAN(10, "扫村", "讯盟");//待确定
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