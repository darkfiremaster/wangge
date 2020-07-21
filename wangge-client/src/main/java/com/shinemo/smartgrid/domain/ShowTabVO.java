package com.shinemo.smartgrid.domain;

import lombok.Data;

/**
 * @Author shangkaihui
 * @Date 2020/7/16 14:58
 * @Desc
 */
@Data
public class ShowTabVO {

    /**
     * 是否展示装维数据看板
     */
    private Boolean showZhuangWeiDataBoard;

    /**
     * 是否展示智慧小屏
     */
    private Boolean showSmartReport;
}
