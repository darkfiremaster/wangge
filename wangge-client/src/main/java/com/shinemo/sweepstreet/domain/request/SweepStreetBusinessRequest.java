package com.shinemo.sweepstreet.domain.request;

import com.shinemo.client.common.BaseDO;
import com.shinemo.sweepstreet.domain.vo.SweepStreetBizDetailVO;
import lombok.Data;

import java.util.List;

/**
 * 类说明: 扫街业务办理请求
 *
 * @author zengpeng
 */
@Data
public class SweepStreetBusinessRequest extends BaseDO {

    /**
     * 扫街活动id
     */
    private Long activityId;

    private List<SweepStreetBizDetailVO> bizList;

    private String remark;
}
