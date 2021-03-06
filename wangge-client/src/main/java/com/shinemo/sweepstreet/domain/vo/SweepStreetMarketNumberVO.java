package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBizDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 类说明: 扫街业务办理VO
 *
 * @author zengpeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SweepStreetMarketNumberVO extends BaseDO {
    /**
     * 业务列表
     */
    private List<SweepStreetBizDetailVO> bizInfoList;

    /**
     * 备注
     */
    private String bizRemark;
}
