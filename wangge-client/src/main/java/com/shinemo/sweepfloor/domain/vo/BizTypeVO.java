package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

@Data
public class BizTypeVO {
    /** 编码 */
    private String code;
    /** 名字 */
    private String name;
    /** 排序 */
    private Integer sort;
    /** 显示标识 */
    private Integer displayFlag;
}
