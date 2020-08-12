package com.shinemo.groupserviceday.domain.request;

import lombok.Data;

import java.util.Date;

@Data
public class GroupServiceListRequest {
    private Integer status;
    private Integer pageSize;
    private Integer currentPage;
    private Long startTime;
    private Long endTime;
}
