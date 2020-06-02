package com.shinemo.sweepfloor.domain.query;

import com.shinemo.common.tools.query.Query;
import lombok.Data;

import java.util.List;

@Data
public class SmartGridActivityQuery extends Query {
    private Long id;

    private Long activityId;

    private String gridId;

    private List<String> gridIds;

    private Integer bizType;
}
