package com.shinemo.stallup.domain.query;

import lombok.Data;

import java.util.List;

@Data
public class StallUpCommunityQuery {
    private Long id;
    private Long activityId;
    private String communityId;
    private List<Long>  activityIds;
    private String mobile;
}
