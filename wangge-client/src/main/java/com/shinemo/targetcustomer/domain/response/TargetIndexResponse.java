package com.shinemo.targetcustomer.domain.response;

import com.shinemo.client.common.BaseDO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 类说明: 指标信息
 *
 * @author 曾鹏
 */
@Setter
@Getter
@Data
public class TargetIndexResponse  extends BaseDO {


    /**
     * 指标ID
     */
    private Long indexId;

    /**
     * 指标名称
     */
    private String indexName;

    /**
     * 指标code
     */
    private String indexCode;

    /**
     * 小区信息列表
     */
    private List<TargetCommunityResponse> communityList;


    /**
     * 指标数据截止日期
     */
    private Date deadlineTime;

}
