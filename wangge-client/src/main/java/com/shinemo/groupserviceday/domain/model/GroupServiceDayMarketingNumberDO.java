package com.shinemo.groupserviceday.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 集团服务日业务办理量表
 *
 * @author zz
 * @date 2020-08-03 17:07:32
 */
@Getter
@Setter
public class GroupServiceDayMarketingNumberDO {


    private Long id;


    private Date gmtCreate;


    private Date gmtModified;


    private Long userId;


    private Long groupServiceDayId;


    private Integer count;


    private String detail;


    private String publicBizRemark;


    private String informationBizRemark;

}
