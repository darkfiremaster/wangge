package com.shinemo.groupserviceday.domain.query;

import com.shinemo.client.common.QueryBase;
import lombok.Data;

import java.util.List;

@Data
public class GroupServiceDayMarketingNumberQuery extends QueryBase {
    private Long id;
    private Long groupServiceDayId;
    private List<Long> groupServiceDayIds;
}
