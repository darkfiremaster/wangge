package com.shinemo.sweepfloor.domain.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BusinessConfigDO {
    private Long id;
    private Integer bizType;
    private Integer type;
    private String content;
    private Date gmtCreate;
    private Date gmtModified;
    private List<Integer> typeList;
}
