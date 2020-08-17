package com.shinemo.sweepstreet.domain.vo;

import com.shinemo.client.common.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类说明:
 * 前端请求的业务实体类
 * @author zengpeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SweepStreetBizDetailVO extends BaseDO {

    private Long id;
    private String name;
    private Integer num;
}
