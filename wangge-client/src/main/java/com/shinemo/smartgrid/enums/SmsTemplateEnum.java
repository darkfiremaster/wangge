package com.shinemo.smartgrid.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @Author shangkaihui
 * @Date 2020/6/10 14:05
 * @Desc 短信模板
 */
@AllArgsConstructor
@Getter
public enum SmsTemplateEnum {

    STALL_UP_START(232,"摆摊-预定开始时间未签到","{摆摊标题}的摆摊已开始，您还未参与打卡。请尽快前往参与营销。"),
    STALL_UP_END(233,"摆摊-预定结束时间未签出","{摆摊标题}的摆摊按照计划已结束，离开营销现场时不要忘记提交营销反馈哟。"),
    DAOSANJIAO_WARN(234,"倒三角工单-执行时间前4小时提醒（休息时间不做提醒中午12:00-14:30；18:00-次日8:30）","您有一条倒三角支撑工单，剩余处理时限4小时，请尽快处理。"),


    ;

    /**
     * 模板id
     */
    private Integer templateId;

    /**
     * 短信模板名称
     */
    private String name;

    /**
     * 短信模板内容
     */
    private String content;

}

