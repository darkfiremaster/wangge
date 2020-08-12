package com.shinemo.groupserviceday.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类说明: 集团服务日业务办理VO
 *
 * @author zengpeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupServiceDayMarketNumberVO {

    /**
     * 大众市场业务列表
     */
    private List<GroupServiceDayBizDetailVO> publicBizInfoList;

    /**
     * 大众市场业务备注
     */
    private String publicBizRemark;

    /**
     * 信息化业务列表
     */
    private List<GroupServiceDayBizDetailVO> informationBizInfoList;

    /**
     * 信息化业务备注
     */
    private String informationBizRemark;
}
