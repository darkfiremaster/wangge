package com.shinemo.groupserviceday.domain.request;

import com.shinemo.groupserviceday.domain.vo.GroupServiceDayMarketNumberVO;
import lombok.Data;

import java.util.List;

/**
 * 类说明: 业务录入请求类
 *
 * @author zengpeng
 */
@Data
public class GroupServiceDayBusinessRequest {
    /**
     * 集团服务日id
     */
    private Long activityId;

    /**
     * 大众业务办理列表
     */
    private List<GroupServiceDayMarketNumberVO> publicBizList;

    /**
     * 大众业务办理备注
     */
    private String publicBizRemark;

    /**
     * 信息化业务办理列表
     */
    private List<GroupServiceDayMarketNumberVO> informationBizLis;

    /**
     * 信息化业务办理备注
     */
    private String informationRemark;

}
