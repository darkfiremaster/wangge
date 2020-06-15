package com.shinemo.todo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/6/2 10:19
 * @Desc
 */
@Data
public class TodoTypeVO implements Serializable {

    private static final long serialVersionUID = -2478129774225303405L;

    private List<TodoTypeListBean> toDoTypeList;


    @Data
    public static class TodoTypeListBean implements Serializable{

        private static final long serialVersionUID = -645282461480703833L;
        private String name;
        private List<ChildListBean> childList;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ChildListBean implements Serializable{

            private static final long serialVersionUID = -2963719378103084559L;

            private String name;
            /**
             * 类型
             */
            private Integer todoType;
            /**
             * 数量
             */
            private Integer count;
        }
    }
}
