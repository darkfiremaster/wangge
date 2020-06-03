package com.shinemo.todo.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 10:19
 * @Desc
 */
@Data
public class TodoTypeVO {

    private List<ToDoTypeListBean> toDoTypeList;


    @Data
    public static class ToDoTypeListBean {
        private String name;
        private List<ChildListBean> childList;

        @Data
        public static class ChildListBean {

            private String name;
            private String toDoType;
        }
    }
}
