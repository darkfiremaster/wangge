package com.shinemo.stallup.domain.huawei;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 17:14
 * @Desc
 */
@Data
public class QueryCommunityResult {

    /**
     * code : 0
     * message : success
     * data : {"custGoupList":[{"custGroupId":1,"custGroupName":"待维护客户-资费不满意"},{"custGroupId":2,"custGroupName":"待维护客户-网络质差"}],"border":["120.065484,31.280426","120.065484,29.280426","119.065484,31.280426","119.065484,29.280426"],"centerpoint":"119.666666,30.280426","opportunityNum":"20","warningNum":"4"}
     */

    private Integer code;
    private String message;
    private DataBean data;


    @Data
    public static class DataBean {
        /**
         * custGoupList : [{"custGroupId":1,"custGroupName":"待维护客户-资费不满意"},{"custGroupId":2,"custGroupName":"待维护客户-网络质差"}]
         * border : ["120.065484,31.280426","120.065484,29.280426","119.065484,31.280426","119.065484,29.280426"]
         * centerpoint : 119.666666,30.280426
         * opportunityNum : 20
         * warningNum : 4
         */

        private String centerPoint;
        private String opportunityNum;
        private String warningNum;
        private List<CustGoupListBean> custGoupList;
        private String border;


        @Data
        public static class CustGoupListBean {
            /**
             * custGroupId : 1
             * custGroupName : 待维护客户-资费不满意
             */
            private String custGroupId;
            private String custGroupName;
            private String count;
        }
    }
}
