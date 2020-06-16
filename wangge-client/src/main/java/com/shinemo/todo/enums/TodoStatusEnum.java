package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoStatusEnum {
    NOT_FINISH(0, "未完成"),
    OTHER(1, "其他");

    private int id;
    private String desc;

    public static TodoStatusEnum getById(int id) {
        TodoStatusEnum[] e = TodoStatusEnum.values();
        for (TodoStatusEnum iter : e) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }
    
    
}