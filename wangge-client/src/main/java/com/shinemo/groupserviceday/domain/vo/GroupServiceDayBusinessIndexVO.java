package com.shinemo.groupserviceday.domain.vo;

import com.shinemo.stallup.domain.model.StallUpBizType;
import lombok.Data;

import java.util.List;

/**
 * 类说明: 业务首页
 *
 * @author zengpeng
 */
@Data
public class GroupServiceDayBusinessIndexVO {

    /**
     * 集团服务日 工具栏
     */
    private List<StallUpBizType> marketToolList;

    /**
     * 集团服务日 大众业务栏
     */
    private List<StallUpBizType> publicMarketBizList;

    /**
     * 集团服务日 政企专属栏
     */
    private List<StallUpBizType> informationBizList;

    /**
     * 集团服务日 大众业务栏 数据头
     */
    private List<StallUpBizType> publicMarketBizDataList;

    /**
     * 集团服务日 政企专属栏 数据头
     */
    private List<StallUpBizType> informationBizDataList;
}
