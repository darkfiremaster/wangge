package com.shinemo.groupserviceday.domain.vo;

import lombok.Data;


@Data
public class GroupServiceDayVO {

    private Long id;
    /**
     * 集团服务日标题
     */
    private String title;
    /**
     * 集团id
     */
    private String groupId;
    /**
     * 集团名称
     */
    private String groupName;
    /**
     * 集团地址
     */
    private String groupAddress;
    /**
     * 计划开始时间
     */
    private String planStartTime;
    /**
     * 计划结束时间
     */
    private String planEndTime;
    /**
     * 集团坐标
     */
    private String location;
    /**
     * 实际开始时间
     */
    private String realStartTime;
    /**
     * 实际结束时间
     */
    private String realEndTime;
    /** 办理量总计 */
    private Integer businessCount = 0;
}
