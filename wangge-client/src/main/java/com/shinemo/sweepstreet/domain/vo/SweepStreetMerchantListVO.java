package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import lombok.Data;

import java.util.List;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
public class SweepStreetMerchantListVO extends BaseDO {
    private List<SweepStreetMerchantVO> merchantsList;
}
