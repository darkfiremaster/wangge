package com.shinemo.stallup.domain.query;

import lombok.Data;

import java.util.List;

@Data
public class StallUpCommunityQuery {
    private Long id;
    private Long activityId;
    private List<Long>  activityIds;
}
