package com.shinemo.wangge.core.delay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author shangkaihui
 * @Date 2020/8/24 16:32
 * @Desc
 */

@Getter
@AllArgsConstructor
public enum  DelayJobType {
    STALL_UP_STAET(1, "摆摊", "预定开始时间未签到"),
    STALL_UP_END(2, "摆摊", "预定结束时间未签出")


    ;

    private Integer jobType;
    private String bizType;
    private String desc;
}
