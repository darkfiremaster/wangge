package com.shinemo.sweepvillage.domain.request;

import com.shinemo.sweepvillage.domain.vo.SweepVillageBizDetail;
import lombok.Data;

import java.util.List;

/**
 * 类说明:扫村业务
 *
 * @author zengpeng
 */
@Data
public class SweepVillageBusinessRequest {
    private Long activityId;

    /**
     * 业务列表
     */
    private List<SweepVillageBizDetail> bizList;


    /** 备注 */
    private String remark;

    /** 营销人员名 */
    private String username;
}
