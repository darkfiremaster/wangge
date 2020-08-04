package com.shinemo.groupserviceday.domain.model;

import com.shinemo.client.common.BaseDO;
import lombok.*;

import java.util.Date;

/**
 * 集团服务日业务办理量表
 *
 * @author zz
 * @date 2020-08-03 17:07:32
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupServiceDayMarketingNumberDO extends BaseDO {


    private Long id;


    private Date gmtCreate;


    private Date gmtModified;


    private String userId;


    private Long groupServiceDayId;


    private Integer count;


    private String detail;


    private String publicBizRemark;


    private String informationBizRemark;

}
