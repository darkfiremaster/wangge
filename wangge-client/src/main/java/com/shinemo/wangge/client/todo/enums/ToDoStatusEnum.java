package com.shinemo.wangge.client.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ToDoStatusEnum {
    NOT_FINISH(0, "未完成"),
    FINISH(1, "已完成");

    private int id;
    private String desc;

    public static ToDoStatusEnum getById(int id) {
        ToDoStatusEnum[] e = ToDoStatusEnum.values();
        for (ToDoStatusEnum iter : e) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }
    
    
}