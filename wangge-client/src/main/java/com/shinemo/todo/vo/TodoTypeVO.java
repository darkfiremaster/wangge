package com.shinemo.todo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 10:19
 * @Desc
 */
@Data
public class TodoTypeVO {

    private List<TodoTypeListBean> toDoTypeList;


    @Data
    public static class TodoTypeListBean {
        private String name;
        private List<ChildListBean> childList;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ChildListBean {

            private String name;
            private Integer todoType;
        }
    }
}
