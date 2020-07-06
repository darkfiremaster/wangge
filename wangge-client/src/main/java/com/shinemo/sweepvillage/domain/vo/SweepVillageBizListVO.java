package com.shinemo.sweepvillage.domain.vo;

import com.shinemo.stallup.domain.model.StallUpBizType;
import lombok.Data;

import java.util.List;

/**
 * 类说明:扫村情况 vo
 *
 * @author zengpeng
 */
@Data
public class SweepVillageBizListVO {

    /**
     * 扫村情况 工具栏 "智慧查询"
     */
    private List<StallUpBizType> marketToolList;

    /**
     * 扫村情况 业务栏
     */
    private List<StallUpBizType> sweepVillageBizList;

    /**
     * 扫村情况 营销数据栏
     */
    private List<StallUpBizType> sweepVillageList;
}
