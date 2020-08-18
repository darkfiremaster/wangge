package com.shinemo.sweepstreet.domain.model;

import com.shinemo.groupserviceday.domain.model.HuaweiGroupServiceDayBizDetail;
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
public class HuaweiSweepStreetBiz {

    /**
     * 业务列表
     */
    private List<HuaweiSweepStreetBizDetail> bizInfoList;

    /**
     * 备注
     */
    private String bizRemark;



}
