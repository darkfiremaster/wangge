package com.shinemo.sweepfloor.domain.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SmartGridActivityDO {
    private Long id;
    private Integer bizType;
    private Long activityId;
    private String gridId;
    private Date gmtCreate;
    private Date gmtModified;
    private String gridName;
}
