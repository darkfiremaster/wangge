package com.shinemo.groupserviceday.domain.model;

import com.shinemo.groupserviceday.domain.vo.GroupServiceDayBizDetailVO;
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
public class HuaweiGroupServiceDayBiz {

    /**
     * 大众市场业务列表
     */
    private List<HuaweiGroupServiceDayBizDetail> bizInfoList;

    /**
     * 备注
     */
    private String bizRemark;

    /**
     * 业务分类ID
     */
    private String bizTypeId;

}
