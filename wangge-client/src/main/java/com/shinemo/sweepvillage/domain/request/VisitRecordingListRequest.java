package com.shinemo.sweepvillage.domain.request;

import com.shinemo.common.tools.query.Query;
import com.shinemo.smartgrid.domain.QueryBase;
import lombok.Data;

@Data
public class VisitRecordingListRequest extends Query {
    private String tenantsId;
    private Long activityId;
}
