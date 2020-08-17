package com.shinemo.sweepstreet.domain.model;

import lombok.*;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HuaweiSweepStreetBizDetail {
    /**
     * 业务id
     */
    private String bizId;

    /**
     * 业务名称
     */
    private String bizName;

    /**
     * 办理数量
     */
    private String bizCount;
}
