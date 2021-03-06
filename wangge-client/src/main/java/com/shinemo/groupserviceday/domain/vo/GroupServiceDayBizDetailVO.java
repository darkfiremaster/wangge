package com.shinemo.groupserviceday.domain.vo;

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
public class GroupServiceDayBizDetailVO {

    private Long id;
    private String name;
    private Integer num;
}
