package com.shinemo.stallup.domain.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class SmsHotResponse {
    /** 任务id */
    private String activityId;
    /** 覆盖量总计 */
    private Long clientCountTotal;

    private List<SmsData> smsActivityList;

    @Getter
    @Setter
    public class SmsData {
        /** 申请时间 */
        private String applyTime;
        /** 发送时间 */
        private String sendTime;
        /** 状态 */
        private Integer status;
        /** 覆盖客户数 */
        private String clientCount;
    }
}
