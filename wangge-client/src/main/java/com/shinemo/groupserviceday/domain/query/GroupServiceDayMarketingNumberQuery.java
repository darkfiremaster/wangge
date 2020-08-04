package com.shinemo.groupserviceday.domain.query;

import lombok.Data;

import java.util.List;

@Data
public class GroupServiceDayMarketingNumberQuery {
    private Long groupServiceDayId;
    private List<Long> groupServiceDayIds;
}
