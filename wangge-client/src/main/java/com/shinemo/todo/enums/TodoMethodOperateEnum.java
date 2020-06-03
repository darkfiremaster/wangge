package com.shinemo.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoMethodOperateEnum {
    CREATE(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除");

    private int id;
    private String name;

    public static TodoMethodOperateEnum getById(int id) {
        TodoMethodOperateEnum[] e = TodoMethodOperateEnum.values();
        for (TodoMethodOperateEnum iter : e) {
            if (iter.id == id) {
                return iter;
            }
        }
        return null;
    }
    
    
}