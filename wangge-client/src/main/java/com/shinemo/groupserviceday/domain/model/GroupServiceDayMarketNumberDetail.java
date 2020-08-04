package com.shinemo.groupserviceday.domain.model;

import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBizDetailVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 类说明: 集团服务日业务办理VO
 *
 * @author zengpeng
 */
@Data
@Builder
public class GroupServiceDayMarketNumberDetail {

    /**
     * 大众市场业务列表
     */
    private List<GroupServiceDayBizDetailVO> publicBizInfoList;


    /**
     * 信息化业务列表
     */
    private List<GroupServiceDayBizDetailVO> informationBizInfoList;

}
