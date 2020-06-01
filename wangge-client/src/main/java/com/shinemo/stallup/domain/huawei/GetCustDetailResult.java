package com.shinemo.stallup.domain.huawei;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 17:43
 * @Desc
 */
@Data
public class GetCustDetailResult {


    /**
     * code : 0
     * message : success
     * data : {"page":1,"size":10,"total":123,"pageResult":[{"custName":"123","custTel":"138123456"},{"custName":"123","custTel":"138123456"}]}
     */

    private Integer code;
    private String message;
    private DataBean data;

    @Data
    public static class DataBean {
        /**
         * page : 1
         * size : 10
         * total : 123
         * pageResult : [{"custName":"123","custTel":"138123456"},{"custName":"123","custTel":"138123456"}]
         */

        private Integer page;
        private Integer size;
        private Long total;
        private List<PageResultBean> pageResult;


        @Data
        public static class PageResultBean {
            /**
             * custName : 123
             * custTel : 138123456
             */

            private String custName;
            private String custTel;
        }
    }
}
