package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBizDetailVO;
import lombok.Data;

import java.util.List;

/**
 * 类说明: 扫街业务办理VO
 *
 * @author zengpeng
 */
@Data
public class SweepStreetMarketNumberVO extends BaseDO {
    /**
     * 大众市场业务列表
     */
    private List<GroupServiceDayBizDetailVO> bizBizInfoList;

    /**
     * 大众市场业务备注
     */
    private String bizRemark;
}
