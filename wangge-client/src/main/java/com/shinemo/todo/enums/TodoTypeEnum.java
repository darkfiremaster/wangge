package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoTypeEnum {
    //todo 下周要开启
    //WORK_ORDER(1, "工单"),
    PLAN(2, "计划");

    private int id;
    private String name;

    public static TodoTypeEnum getById(int id) {
        TodoTypeEnum[] e = TodoTypeEnum.values();
        for (TodoTypeEnum iter : e) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }
    
    
}