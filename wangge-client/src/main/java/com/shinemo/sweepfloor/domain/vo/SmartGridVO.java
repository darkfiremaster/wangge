package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class SmartGridVO {
    private String id;
    private String name;
    private List<SweepFloorActivityVO> activityVOList;
}
