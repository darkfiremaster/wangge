package com.shinemo.sweepstreet.domain.request;

import lombok.Data;

@Data
public class SweepStreetListRequest {
    private Integer status;
    private Integer pageSize;
    private Integer currentPage;
    private Long startTime;
    private Long endTime;
}
