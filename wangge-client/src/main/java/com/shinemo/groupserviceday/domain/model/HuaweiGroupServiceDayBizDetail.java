package com.shinemo.groupserviceday.domain.model;

import lombok.Builder;
import lombok.Data;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
@Builder
public class HuaweiGroupServiceDayBizDetail {

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
