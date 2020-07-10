package com.shinemo.sweepvillage.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类说明:
 *
 * @author zengpeng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SweepVillageBizDetail {
    private Long id;
    private String name;
    private Integer count;
    private Integer num;
}
