package com.shinemo.stallup.domain.response;

import lombok.Data;

@Data
public class SmsHotResponse {
    /** 申请时间 */
    private String applyTime;
    /** 发送时间 */
    private String sendTime;
    /** 状态 */
    private Integer status;
    /** 覆盖客户数 */
    private String clientCount;
}
