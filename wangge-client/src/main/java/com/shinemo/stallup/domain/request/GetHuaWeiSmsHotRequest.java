package com.shinemo.stallup.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class GetHuaWeiSmsHotRequest {
    /** 活动id集合 */
    private String activityId;
}
