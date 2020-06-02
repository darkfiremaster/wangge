package com.shinemo.sweepfloor.domain.query;

import lombok.Data;

import java.util.List;

@Data
public class BusinessConfigQuery {
    private Long id;
    private Integer bizType;
    private Integer type;
    private List<Integer> typeList;
}
