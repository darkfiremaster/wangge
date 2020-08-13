package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import com.shinemo.stallup.domain.model.StallUpBizType;
import lombok.Data;

import java.util.List;

/**
 * 类说明: 扫街业务首页
 *
 * @author zengpeng
 */
@Data
public class SweepStreetBusinessIndexVO extends BaseDO {
    /**
     * 扫街 工具栏
     */
    private List<StallUpBizType> marketToolList;

    /**
     * 扫街 业务栏
     */
    private List<StallUpBizType> bizMarketBizList;

    /**
     * 扫街 业务栏 数据头
     */
    private List<StallUpBizType> bizMarketBizDataList;
}
