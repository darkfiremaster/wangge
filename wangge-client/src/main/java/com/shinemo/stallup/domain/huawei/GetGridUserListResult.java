package com.shinemo.stallup.domain.huawei;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 18:56
 * @Desc
 */
@Data
public class GetGridUserListResult {


    /**
     * code : 0
     * message : success
     * data : {"userList":[{"userId":1,"userName":"XXX","userRole":"客户经理"}]}
     */
    private Integer code;
    private String message;
    private DataBean data;


    @Data
    public static class DataBean {
        private List<UserListBean> userList;

        @Data
        public static class UserListBean {
            /**
             * userTel : 1
             * userName : XXX
             * userRole : 客户经理
             */
            private String userId;
            private String userTel;
            private String userName;
            private String userRole;

        }
    }
}
