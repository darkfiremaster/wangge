package com.shinemo.wangge.client.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ToDoTypeEnum {
    WORK_ORDER(1, "工单"),
    PLAN(2, "计划");

    private int id;
    private String name;

    public static ToDoTypeEnum getById(int id) {
        ToDoTypeEnum[] e = ToDoTypeEnum.values();
        for (ToDoTypeEnum iter : e) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }
    
    
}