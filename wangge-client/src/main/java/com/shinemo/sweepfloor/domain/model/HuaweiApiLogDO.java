package com.shinemo.sweepfloor.domain.model;

import lombok.Data;

import java.util.Date;

@Data
public class HuaweiApiLogDO {
    private Long id;
    private Integer status;
    private String mobile;
    private Long costTime;
    private String request;
    private String response;
    private Date gmtCreate;
    private Date gmtModified;
    private String url;
    private String tableIndex;
}
